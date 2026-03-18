/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.febit.wit.extern.asm;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.febit.wit.parser.NativeFunctionFactory;
import org.febit.wit.parser.ReflectNativeFunctionFactory;
import org.febit.wit.runtime.WitFunction;
import org.febit.wit.util.ClassUtils;
import org.febit.wit_shaded.asm.ClassWriter;
import org.febit.wit_shaded.asm.Constants;
import org.febit.wit_shaded.asm.Label;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

@Slf4j
@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "create")
public class AsmNativeFunctionFactory implements NativeFunctionFactory.Decorator {

    private static final String[] FUNC_DECLARE = {"org/febit/wit/runtime/WitFunction"};

    @Getter
    private final NativeFunctionFactory delegate;

    public static AsmNativeFunctionFactory create() {
        return create(ReflectNativeFunctionFactory.INSTANCE);
    }

    @Override
    public WitFunction constructor(Constructor<?> constructor) {
        var function = checkAndConstruct(constructor);
        if (function != null) {
            return function;
        }
        return delegate.constructor(constructor);
    }

    @Override
    public WitFunction method(Method method) {
        var function = checkAndConstruct(method);
        if (function != null) {
            return function;
        }
        return delegate.method(method);
    }

    @Nullable
    private WitFunction checkAndConstruct(Member member) {
        if (!ClassUtils.isPublic(member.getDeclaringClass()) || !ClassUtils.isPublic(member)) {
            return null;
        }
        try {
            return construct(member);
        } catch (Exception | LinkageError e) {
            log.error("Cannot construct AsmFunction for '{}'.", member, e);
            return null;
        }
    }

    @SuppressWarnings({
            "squid:S3776" // Cognitive Complexity of methods should not be too high
    })
    static WitFunction construct(Member obj)
            throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        var className = "org.febit.wit.extern.asm.AsmFunction" + AsmUtils.SEQ.getAndIncrement();
        var classWriter = new ClassWriter(Constants.V1_5, Constants.ACC_PUBLIC + Constants.ACC_FINAL,
                AsmUtils.toInternalName(className), AsmUtils.TYPE_OBJ, FUNC_DECLARE);

        AsmUtils.visitConstructor(classWriter);

        final boolean isInterface;
        final boolean isStatic;
        final boolean isConstructor;
        final String ownerClass;
        final String destName;
        final String destDesc;
        final Class<?>[] paramTypes;
        final Class<?> returnType;

        if (obj instanceof Method method) {
            isInterface = method.getDeclaringClass().isInterface();
            isStatic = ClassUtils.isStatic(method);
            isConstructor = false;
            ownerClass = AsmUtils.toInternalName(method.getDeclaringClass().getName());
            destName = method.getName();
            destDesc = AsmUtils.getDescriptor(method);
            paramTypes = method.getParameterTypes();
            returnType = method.getReturnType();
        } else if (obj instanceof Constructor<?> constructor) {
            isInterface = false;
            isStatic = false;
            isConstructor = true;
            ownerClass = AsmUtils.toInternalName(constructor.getDeclaringClass().getName());
            destName = AsmUtils.METHOD_CTOR;
            destDesc = AsmUtils.getDescriptor(constructor);
            paramTypes = constructor.getParameterTypes();
            returnType = constructor.getDeclaringClass();
        } else {
            throw new IllegalArgumentException("Unsupported member type: " + obj.getClass());
        }

        var paramTypesLen = paramTypes.length;
        var m = classWriter.visitMethod(Constants.ACC_PUBLIC, "apply",
                "(Lorg/febit/wit/runtime/InternalContext;[Ljava/lang/Object;)Ljava/lang/Object;", null);

        if (paramTypesLen == 0) {
            if (isStatic) {
                m.invokeStatic(ownerClass, destName, destDesc);
                AsmUtils.visitBoxIfNeed(m, returnType);
                m.visitInsn(Constants.ARETURN);
            } else if (isConstructor) {
                m.visitTypeInsn(Constants.NEW, ownerClass);
                m.visitInsn(Constants.DUP);
                m.visitMethodInsn(Constants.INVOKESPECIAL, ownerClass, AsmUtils.METHOD_CTOR, "()V");
                m.visitInsn(Constants.ARETURN);
            } else {
                Label toException = new Label();
                m.visitVarInsn(Constants.ALOAD, 2);
                m.visitJumpInsn(Constants.IFNULL, toException);
                m.visitVarInsn(Constants.ALOAD, 2);
                m.visitInsn(Constants.ARRAYLENGTH);
                m.visitJumpInsn(Constants.IFEQ, toException);
                m.visitVarInsn(Constants.ALOAD, 2);
                m.visitInsn(Constants.ICONST_0);
                m.visitInsn(Constants.AALOAD);
                m.visitJumpInsn(Constants.IFNULL, toException);
                m.visitVarInsn(Constants.ALOAD, 2);
                m.visitInsn(Constants.ICONST_0);
                m.visitInsn(Constants.AALOAD);
                m.checkCast(ownerClass);
                m.visitMethodInsn(isInterface ? Constants.INVOKEINTERFACE
                        : Constants.INVOKEVIRTUAL, ownerClass, destName, destDesc);
                AsmUtils.visitBoxIfNeed(m, returnType);
                m.visitInsn(Constants.ARETURN);
                m.visitLabel(toException);
                AsmUtils.visitScriptEvaluateException(m, "First argument can't be null.");
            }
        } else {
            if (isConstructor) {
                m.visitTypeInsn(Constants.NEW, ownerClass);
                m.visitInsn(Constants.DUP);
            }

            m.visitVarInsn(Constants.ALOAD, 2);

            m.push(isStatic || isConstructor ? paramTypesLen : paramTypesLen + 1);
            m.invokeStatic("org/febit/wit/util/Args", "ensureSize",
                    "([Ljava/lang/Object;I)[Ljava/lang/Object;");
            m.visitVarInsn(Constants.ASTORE, 2);

            int paramCount = 0;
            if (!isStatic && !isConstructor) {
                m.visitVarInsn(Constants.ALOAD, 2);
                m.visitInsn(Constants.ICONST_0);
                m.visitInsn(Constants.AALOAD);
                m.checkCast(ownerClass);
                paramCount++;
            }

            for (Class<?> paramType : paramTypes) {
                m.visitVarInsn(Constants.ALOAD, 2);
                m.push(paramCount);
                m.visitInsn(Constants.AALOAD);
                m.checkCast(AsmUtils.toBoxedInternalName(paramType));
                AsmUtils.visitUnboxIfNeed(m, paramType);
                paramCount++;
            }

            @SuppressWarnings({
                    "squid:S3358" // Ternary operators should not be nested
            })
            var opCode = isStatic ? Constants.INVOKESTATIC
                    : isConstructor
                    ? Constants.INVOKESPECIAL
                    : isInterface
                    ? Constants.INVOKEINTERFACE
                    : Constants.INVOKEVIRTUAL;
            //Invoke Method
            m.visitMethodInsn(opCode, ownerClass, destName, destDesc);
            AsmUtils.visitBoxIfNeed(m, returnType);
            m.visitInsn(Constants.ARETURN);
        }
        m.visitMaxs();

        return (WitFunction) AsmUtils.loadClass(className, classWriter)
                .getConstructor().newInstance();
    }
}
