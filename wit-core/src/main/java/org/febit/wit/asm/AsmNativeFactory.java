// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.asm;

import org.febit.wit.core.NativeFactory;
import org.febit.wit.lang.MethodDeclare;
import org.febit.wit.util.ClassUtil;
import org.febit.wit_shaded.asm.ClassWriter;
import org.febit.wit_shaded.asm.Constants;
import org.febit.wit_shaded.asm.Label;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

/**
 * @author zqq90
 */
public class AsmNativeFactory extends NativeFactory {

    private static final String[] METHOD_DECLARE = {"org/febit/wit/lang/MethodDeclare"};

    @Override
    protected MethodDeclare createNativeConstructorDeclare(Constructor constructor) {
        var accessor = createMethodDeclare(constructor);
        if (accessor != null) {
            return accessor;
        }
        return super.createNativeConstructorDeclare(constructor);
    }

    @Override
    public MethodDeclare createNativeMethodDeclare(Method method) {
        var accessor = createMethodDeclare(method);
        if (accessor != null) {
            return accessor;
        }
        return super.getNativeMethodDeclare(method);
    }

    protected MethodDeclare createMethodDeclare(Member member) {
        if (!ClassUtil.isPublic(member.getDeclaringClass()) || !ClassUtil.isPublic(member)) {
            return null;
        }
        MethodDeclare declare = methodCaching.get(member);
        if (declare == null) {
            synchronized (this) {
                try {
                    declare = methodCaching.get(member);
                    if (declare == null) {
                        declare = createAccessor(member);
                        methodCaching.put(member, declare);
                    }
                } catch (Exception | LinkageError e) {
                    logger.error("Failed to create ASMMethodDeclare for '" + member + "'.", e);
                }
            }
        }
        return declare;
    }

    @SuppressWarnings({
            "squid:S3776" // Cognitive Complexity of methods should not be too high
    })
    static MethodDeclare createAccessor(Member obj)
            throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        var className = "org.febit.wit.asm.Accessor" + AsmUtil.NEXT_SN.getAndIncrement();
        var classWriter = new ClassWriter(Constants.V1_5, Constants.ACC_PUBLIC + Constants.ACC_FINAL,
                AsmUtil.getInternalName(className), "java/lang/Object", METHOD_DECLARE);

        AsmUtil.visitConstructor(classWriter);

        final boolean isInterface;
        final boolean isStatic;
        final boolean isConstructor;
        final String ownerClass;
        final String destName;
        final String destDesc;
        final Class[] paramTypes;
        final Class<?> returnType;

        if (obj instanceof Method) {
            Method method = (Method) obj;
            isInterface = method.getDeclaringClass().isInterface();
            isStatic = ClassUtil.isStatic(method);
            isConstructor = false;
            ownerClass = AsmUtil.getInternalName(method.getDeclaringClass().getName());
            destName = method.getName();
            destDesc = AsmUtil.getDescriptor(method);
            paramTypes = method.getParameterTypes();
            returnType = method.getReturnType();
        } else {
            Constructor constructor = (Constructor) obj;
            isInterface = false;
            isStatic = false;
            isConstructor = true;
            ownerClass = AsmUtil.getInternalName(constructor.getDeclaringClass().getName());
            destName = AsmUtil.METHOD_CTOR;
            destDesc = AsmUtil.getDescriptor(constructor);
            paramTypes = constructor.getParameterTypes();
            returnType = constructor.getDeclaringClass();
        }

        var paramTypesLen = paramTypes.length;
        var m = classWriter.visitMethod(Constants.ACC_PUBLIC, "invoke",
                "(Lorg/febit/wit/InternalContext;[Ljava/lang/Object;)Ljava/lang/Object;", null);

        if (paramTypesLen == 0) {
            if (isStatic) {
                m.invokeStatic(ownerClass, destName, destDesc);
                AsmUtil.visitBoxIfNeed(m, returnType);
                m.visitInsn(Constants.ARETURN);
            } else if (isConstructor) {
                m.visitTypeInsn(Constants.NEW, ownerClass);
                m.visitInsn(Constants.DUP);
                m.visitMethodInsn(Constants.INVOKESPECIAL, ownerClass, AsmUtil.METHOD_CTOR, "()V");
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
                AsmUtil.visitBoxIfNeed(m, returnType);
                m.visitInsn(Constants.ARETURN);
                m.visitLabel(toException);
                AsmUtil.visitScriptRuntimeException(m, "First argument can't be null.");
            }
        } else {
            if (isConstructor) {
                m.visitTypeInsn(Constants.NEW, ownerClass);
                m.visitInsn(Constants.DUP);
            }

            m.visitVarInsn(Constants.ALOAD, 2);

            m.push(isStatic || isConstructor ? paramTypesLen : paramTypesLen + 1);
            m.invokeStatic("org/febit/wit/util/ArrayUtil", "ensureMinSize",
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
                m.checkCast(AsmUtil.getBoxedInternalName(paramType));
                AsmUtil.visitUnboxIfNeed(m, paramType);
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
            AsmUtil.visitBoxIfNeed(m, returnType);
            m.visitInsn(Constants.ARETURN);
        }
        m.visitMaxs();

        return (MethodDeclare) AsmUtil.loadClass(className, classWriter)
                .getConstructor().newInstance();
    }
}
