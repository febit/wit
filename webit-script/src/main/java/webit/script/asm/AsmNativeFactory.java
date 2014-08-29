// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.asm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import webit.script.asm.lib.ClassWriter;
import webit.script.asm.lib.Constants;
import webit.script.asm.lib.Label;
import webit.script.asm.lib.MethodWriter;
import webit.script.core.NativeFactory;
import webit.script.lang.MethodDeclare;
import webit.script.util.ClassUtil;

/**
 *
 * @author zqq
 */
public class AsmNativeFactory extends NativeFactory {

    private static final String[] ASM_METHOD_ACCESSOR = new String[]{"webit/script/lang/MethodDeclare"};

    @Override
    public MethodDeclare createNativeConstructorDeclare(Class type, Constructor constructor, int line, int column) {
        if (ClassUtil.isPublic(type) && ClassUtil.isPublic(constructor)) {
            try {
                MethodDeclare accessor = createAccessor(constructor);
                if (accessor != null) {
                    return accessor;
                }
            } catch (Throwable e) {
                logger.error("Failed to create ASMMethodDeclare for '{}'.", constructor, e);
            }
        }
        return super.createNativeConstructorDeclare(type, constructor, line, column);
    }

    @Override
    public MethodDeclare createNativeMethodDeclare(Class type, Method method, int line, int column) {
        if (ClassUtil.isPublic(type) && ClassUtil.isPublic(method)) {
            try {
                MethodDeclare accessor = createAccessor(method);
                if (accessor != null) {
                    return accessor;
                }
            } catch (Throwable e) {
                logger.error("Failed to create ASMMethodDeclare for '{}'.", method, e);
            }
        }
        return super.createNativeMethodDeclare(type, method, line, column);
    }

    static MethodDeclare createAccessor(Member obj) throws InstantiationException, IllegalAccessException {
        final String className = "webit.script.asm.Accessor_".concat(ASMUtil.getSn());

        final ClassWriter classWriter = new ClassWriter(Constants.V1_5, Constants.ACC_PUBLIC + Constants.ACC_FINAL, ASMUtil.getInternalName(className), "java/lang/Object", ASM_METHOD_ACCESSOR);
        ASMUtil.visitConstructor(classWriter);

        if (obj instanceof Method) {
            Method method = (Method) obj;
            appendExecuteMethod(
                    classWriter,
                    method.getDeclaringClass().isInterface(),
                    ClassUtil.isStatic(method),
                    false,
                    ASMUtil.getInternalName(method.getDeclaringClass().getName()),
                    method.getName(),
                    ASMUtil.getDescriptor(method),
                    method.getParameterTypes(),
                    method.getReturnType()
            );
        } else {
            Constructor constructor = (Constructor) obj;
            appendExecuteMethod(
                    classWriter,
                    false,
                    false,
                    true,
                    ASMUtil.getInternalName(constructor.getDeclaringClass().getName()),
                    "<init>",
                    ASMUtil.getDescriptor(constructor),
                    constructor.getParameterTypes(),
                    constructor.getDeclaringClass()
            );
        }
        return (MethodDeclare) ASMUtil.loadClass(className, classWriter).newInstance();
    }

    private static void appendExecuteMethod(
            final ClassWriter classWriter,
            final boolean isInterface,
            final boolean isStatic,
            final boolean isConstructor,
            final String ownerClass,
            final String destName,
            final String destDesc,
            final Class[] paramTypes,
            final Class returnType
    ) {
        final MethodWriter m = classWriter.visitMethod(Constants.ACC_PUBLIC, "invoke", "(Lwebit/script/Context;[Ljava/lang/Object;)Ljava/lang/Object;", null);

        final int paramTypesLen = paramTypes.length;
        if (paramTypesLen == 0) {
            if (isStatic) {
                m.invokeStatic(ownerClass, destName, destDesc);
                ASMUtil.visitBoxIfNeed(m, returnType);
                m.visitInsn(Constants.ARETURN);
            } else if (isConstructor) {
                m.visitTypeInsn(Constants.NEW, ownerClass);
                m.visitInsn(Constants.DUP);
                m.visitMethodInsn(Constants.INVOKESPECIAL, ownerClass, "<init>", "()V");
                m.visitInsn(Constants.ARETURN);
            } else {
                Label toExcaption = new Label();
                m.visitVarInsn(Constants.ALOAD, 2);
                m.visitJumpInsn(Constants.IFNULL, toExcaption);
                m.visitVarInsn(Constants.ALOAD, 2);
                m.visitInsn(Constants.ARRAYLENGTH);
                m.visitJumpInsn(Constants.IFEQ, toExcaption);
                m.visitVarInsn(Constants.ALOAD, 2);
                m.visitInsn(Constants.ICONST_0);
                m.visitInsn(Constants.AALOAD);
                m.visitJumpInsn(Constants.IFNULL, toExcaption);
                m.visitVarInsn(Constants.ALOAD, 2);
                m.visitInsn(Constants.ICONST_0);
                m.visitInsn(Constants.AALOAD);
                m.checkCast(ownerClass);
                m.visitMethodInsn(isInterface ? Constants.INVOKEINTERFACE
                        : Constants.INVOKEVIRTUAL, ownerClass, destName, destDesc);
                ASMUtil.visitBoxIfNeed(m, int.class);
                m.visitInsn(Constants.ARETURN);
                m.visitLabel(toExcaption);
                ASMUtil.visitScriptRuntimeException(m, "First argument can't be null.");
            }
        } else {
            if (isConstructor) {
                m.visitTypeInsn(Constants.NEW, ownerClass);
                m.visitInsn(Constants.DUP);
            }

            m.visitVarInsn(Constants.ALOAD, 2);

            m.push(isStatic || isConstructor ? paramTypesLen : paramTypesLen + 1);
            m.invokeStatic("webit/script/util/ArrayUtil", "ensureMinSize", "([Ljava/lang/Object;I)[Ljava/lang/Object;");
            m.visitVarInsn(Constants.ASTORE, 2);

            int i_args = 0;
            if (!isStatic && !isConstructor) {
                m.visitVarInsn(Constants.ALOAD, 2);
                m.visitInsn(Constants.ICONST_0);
                m.visitInsn(Constants.AALOAD);
                m.checkCast(ownerClass);
                i_args = 1;
            }

            for (Class paramType : paramTypes) {
                m.visitVarInsn(Constants.ALOAD, 2);
                m.push(i_args);
                m.visitInsn(Constants.AALOAD);
                m.checkCast(ASMUtil.getBoxedInternalName(paramType));
                ASMUtil.visitUnboxIfNeed(m, paramType);
                i_args++;
            }

            //Invoke Method
            m.visitMethodInsn(isStatic ? Constants.INVOKESTATIC
                    : isConstructor ? Constants.INVOKESPECIAL : isInterface ? Constants.INVOKEINTERFACE
                    : Constants.INVOKEVIRTUAL, ownerClass, destName, destDesc);
            ASMUtil.visitBoxIfNeed(m, returnType);
            m.visitInsn(Constants.ARETURN);
        }
        m.visitMaxs();
    }
}
