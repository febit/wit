// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
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
 * @author zqq90
 */
public class AsmNativeFactory extends NativeFactory {

    private static final String[] METHOD_DECLARE = new String[]{"webit/script/lang/MethodDeclare"};

    @Override
    protected MethodDeclare createNativeConstructorDeclare(Constructor constructor) {
        MethodDeclare accessor = createMethodDeclare(constructor);
        if (accessor != null) {
            return accessor;
        }
        return super.createNativeConstructorDeclare(constructor);
    }

    @Override
    protected MethodDeclare createNativeMethodDeclare(Method method) {
        MethodDeclare accessor = createMethodDeclare(method);
        if (accessor != null) {
            return accessor;
        }
        return super.getNativeMethodDeclare(method);
    }

    protected MethodDeclare createMethodDeclare(Member member) {
        if (ClassUtil.isPublic(member.getDeclaringClass()) && ClassUtil.isPublic(member)) {
            MethodDeclare declare = CACHE.get(member);
            if (declare == null) {
                synchronized (CACHE) {
                    try {
                        declare = CACHE.get(member);
                        if (declare == null) {
                            declare = createAccessor(member);
                            CACHE.put(member, declare);
                        }
                    } catch (Exception | Error e) {
                        logger.error("Failed to create ASMMethodDeclare for '{}'.", member, e);
                    }
                }
            }
            return declare;
        }
        return null;
    }

    static MethodDeclare createAccessor(Member obj) throws InstantiationException, IllegalAccessException {
        final String className = "webit.script.asm.Accessor".concat(ASMUtil.getSn());
        final ClassWriter classWriter = new ClassWriter(Constants.V1_5, Constants.ACC_PUBLIC + Constants.ACC_FINAL, ASMUtil.getInternalName(className), "java/lang/Object", METHOD_DECLARE);

        ASMUtil.visitConstructor(classWriter);

        final boolean isInterface;
        final boolean isStatic;
        final boolean isConstructor;
        final String ownerClass;
        final String destName;
        final String destDesc;
        final Class[] paramTypes;
        final Class returnType;

        if (obj instanceof Method) {
            Method method = (Method) obj;
            isInterface = method.getDeclaringClass().isInterface();
            isStatic = ClassUtil.isStatic(method);
            isConstructor = false;
            ownerClass = ASMUtil.getInternalName(method.getDeclaringClass().getName());
            destName = method.getName();
            destDesc = ASMUtil.getDescriptor(method);
            paramTypes = method.getParameterTypes();
            returnType = method.getReturnType();
        } else {
            Constructor constructor = (Constructor) obj;
            isInterface = false;
            isStatic = false;
            isConstructor = true;
            ownerClass = ASMUtil.getInternalName(constructor.getDeclaringClass().getName());
            destName = "<init>";
            destDesc = ASMUtil.getDescriptor(constructor);
            paramTypes = constructor.getParameterTypes();
            returnType = constructor.getDeclaringClass();
        }

        final int paramTypesLen = paramTypes.length;
        final MethodWriter m = classWriter.visitMethod(Constants.ACC_PUBLIC, "invoke", "(Lwebit/script/Context;[Ljava/lang/Object;)Ljava/lang/Object;", null);

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
                ASMUtil.visitBoxIfNeed(m, returnType);
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

            int paramCount = 0;
            if (!isStatic && !isConstructor) {
                m.visitVarInsn(Constants.ALOAD, 2);
                m.visitInsn(Constants.ICONST_0);
                m.visitInsn(Constants.AALOAD);
                m.checkCast(ownerClass);
                paramCount++;
            }

            for (Class paramType : paramTypes) {
                m.visitVarInsn(Constants.ALOAD, 2);
                m.push(paramCount);
                m.visitInsn(Constants.AALOAD);
                m.checkCast(ASMUtil.getBoxedInternalName(paramType));
                ASMUtil.visitUnboxIfNeed(m, paramType);
                paramCount++;
            }

            //Invoke Method
            m.visitMethodInsn(isStatic ? Constants.INVOKESTATIC
                    : isConstructor ? Constants.INVOKESPECIAL : isInterface ? Constants.INVOKEINTERFACE
                    : Constants.INVOKEVIRTUAL, ownerClass, destName, destDesc);
            ASMUtil.visitBoxIfNeed(m, returnType);
            m.visitInsn(Constants.ARETURN);
        }
        m.visitMaxs();

        return (MethodDeclare) ASMUtil.loadClass(className, classWriter).newInstance();
    }
}
