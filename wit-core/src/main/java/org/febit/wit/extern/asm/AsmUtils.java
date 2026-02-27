// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.extern.asm;

import lombok.experimental.UtilityClass;
import org.febit.wit.util.ClassUtils;
import org.febit.wit_shaded.asm.ClassWriter;
import org.febit.wit_shaded.asm.Constants;
import org.febit.wit_shaded.asm.MethodWriter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;

@UtilityClass
class AsmUtils {

    static final String TYPE_STRING_NAME = "java/lang/String";
    static final String METHOD_CTOR = "<init>";
    static final AtomicLong SEQ = new AtomicLong(1);

    private static final AsmClassLoader CLASS_LOADER = new AsmClassLoader();

    static Class<?> loadClass(String name, ClassWriter classWriter) {
        return CLASS_LOADER.loadClass(name, classWriter.toByteArray());
    }

    static String getBoxedInternalName(Class<?> type) {
        var boxed = ClassUtils.getBoxedPrimitiveClass(type);
        var name = boxed != null
                ? boxed.getName()
                : type.getName();
        return getInternalName(name);
    }

    static String getInternalName(String className) {
        int i = className.indexOf('.');
        if (i < 0) {
            return className;
        }
        char[] str = className.toCharArray();
        int len = str.length;
        for (; i < len; i++) {
            if (str[i] == '.') {
                str[i] = '/';
            }
        }
        return new String(str);
    }

    static String getDescriptor(Constructor<?> c) {
        StringBuilder buf = new StringBuilder();
        buf.append('(');
        for (Class<?> paramType : c.getParameterTypes()) {
            buf.append(getDescriptor(paramType));
        }
        return buf.append(")V").toString();
    }

    static String getDescriptor(Method m) {
        StringBuilder buf = new StringBuilder();
        buf.append('(');
        for (Class<?> paramType : m.getParameterTypes()) {
            buf.append(getDescriptor(paramType));
        }
        return buf.append(')').append(getDescriptor(m.getReturnType())).toString();
    }

    static String getDescriptor(Class<?> c) {
        if (c.isPrimitive()) {
            return String.valueOf(ClassUtils.getAliasOfBaseType(c.getName()));
        }
        String internalName = getInternalName(c.getName());
        if (c.isArray()) {
            return internalName;
        }
        return "L" + internalName + ';';
    }

    static void visitBoxIfNeed(MethodWriter m, Class<?> type) {
        if (!type.isPrimitive()) {
            return;
        }
        if (type == void.class) {
            m.visitFieldInsn(Constants.GETSTATIC, "org/febit/wit/runtime/Undefined",
                    "UNDEFINED", "Lorg/febit/wit/runtime/Undefined;");
            return;
        }
        String boxedType = getBoxedInternalName(type);
        m.invokeStatic(boxedType, "valueOf", "(" + getDescriptor(type) + ")L" + boxedType + ";");
    }

    static void visitUnboxIfNeed(MethodWriter m, Class<?> type) {
        if (!type.isPrimitive()) {
            return;
        }
        if (type == void.class) {
            // ignore void.class
            return;
        }
        m.invokeVirtual(getBoxedInternalName(type), type.getName() + "Value", "()" + getDescriptor(type));
    }

    static void visitScriptEvaluateException(MethodWriter m, String message) {
        m.visitTypeInsn(Constants.NEW, "org/febit/wit/exception/ScriptEvaluateException");
        m.visitInsn(Constants.DUP);
        m.visitLdcInsn(message);
        m.visitMethodInsn(Constants.INVOKESPECIAL, "org/febit/wit/exception/ScriptEvaluateException",
                METHOD_CTOR, "(Ljava/lang/String;)V");
        m.visitInsn(Constants.ATHROW);
    }

    static void visitConstructor(ClassWriter classWriter) {
        MethodWriter m = classWriter.visitMethod(Constants.ACC_PUBLIC, METHOD_CTOR, "()V", null);
        m.visitVarInsn(Constants.ALOAD, 0);
        m.visitMethodInsn(Constants.INVOKESPECIAL, "java/lang/Object", METHOD_CTOR, "()V");
        m.visitInsn(Constants.RETURN);
        m.visitMaxs();
    }

    private static final class AsmClassLoader extends ClassLoader {

        AsmClassLoader() {
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            return ClassUtils.getDefaultClassLoader().loadClass(name);
        }

        Class<?> loadClass(String name, byte[] b) throws ClassFormatError {
            return defineClass(name, b, 0, b.length, null);
        }
    }
}
