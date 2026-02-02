// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.asm;

import lombok.extern.slf4j.Slf4j;
import org.febit.wit.core.NativeFactory;
import org.febit.wit.lang.FunctionDeclare;
import org.febit.wit.security.NativeSecurity;
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
public class AsmNativeFactory extends NativeFactory {

    private static final String[] FUNC_DECLARE = {"org/febit/wit/lang/FunctionDeclare"};

    public AsmNativeFactory(NativeSecurity security) {
        super(security);
    }

    @Override
    protected FunctionDeclare createNativeConstructorDeclare(Constructor<?> constructor) {
        var accessor = createMethodDeclare(constructor);
        if (accessor != null) {
            return accessor;
        }
        return super.createNativeConstructorDeclare(constructor);
    }

    @Override
    public FunctionDeclare createNativeMethodDeclare(Method method) {
        var accessor = createMethodDeclare(method);
        if (accessor != null) {
            return accessor;
        }
        return super.getNativeMethodDeclare(method);
    }

    @Nullable
    protected FunctionDeclare createMethodDeclare(Member member) {
        if (!ClassUtils.isPublic(member.getDeclaringClass()) || !ClassUtils.isPublic(member)) {
            return null;
        }
        FunctionDeclare declare = methodCaching.get(member);
        if (declare == null) {
            synchronized (this) {
                try {
                    declare = methodCaching.get(member);
                    if (declare == null) {
                        declare = createAccessor(member);
                        methodCaching.put(member, declare);
                    }
                } catch (Exception | LinkageError e) {
                    log.error("Failed to create ASMFunctionDeclare for '" + member + "'.", e);
                }
            }
        }
        return declare;
    }

    @SuppressWarnings({
            "squid:S3776" // Cognitive Complexity of methods should not be too high
    })
    static FunctionDeclare createAccessor(Member obj)
            throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        var className = "org.febit.wit.asm.Accessor" + AsmUtils.NEXT_SN.getAndIncrement();
        var classWriter = new ClassWriter(Constants.V1_5, Constants.ACC_PUBLIC + Constants.ACC_FINAL,
                AsmUtils.getInternalName(className), "java/lang/Object", FUNC_DECLARE);

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
            ownerClass = AsmUtils.getInternalName(method.getDeclaringClass().getName());
            destName = method.getName();
            destDesc = AsmUtils.getDescriptor(method);
            paramTypes = method.getParameterTypes();
            returnType = method.getReturnType();
        } else if (obj instanceof Constructor<?> constructor) {
            isInterface = false;
            isStatic = false;
            isConstructor = true;
            ownerClass = AsmUtils.getInternalName(constructor.getDeclaringClass().getName());
            destName = AsmUtils.METHOD_CTOR;
            destDesc = AsmUtils.getDescriptor(constructor);
            paramTypes = constructor.getParameterTypes();
            returnType = constructor.getDeclaringClass();
        } else {
            throw new IllegalArgumentException("Unsupported member type: " + obj.getClass());
        }

        var paramTypesLen = paramTypes.length;
        var m = classWriter.visitMethod(Constants.ACC_PUBLIC, "invoke",
                "(Lorg/febit/wit/InternalContext;[Ljava/lang/Object;)Ljava/lang/Object;", null);

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
                AsmUtils.visitScriptRuntimeException(m, "First argument can't be null.");
            }
        } else {
            if (isConstructor) {
                m.visitTypeInsn(Constants.NEW, ownerClass);
                m.visitInsn(Constants.DUP);
            }

            m.visitVarInsn(Constants.ALOAD, 2);

            m.push(isStatic || isConstructor ? paramTypesLen : paramTypesLen + 1);
            m.invokeStatic("org/febit/wit/util/ArrayUtils", "ensureMinSize",
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
                m.checkCast(AsmUtils.getBoxedInternalName(paramType));
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

        return (FunctionDeclare) AsmUtils.loadClass(className, classWriter)
                .getConstructor().newInstance();
    }
}
