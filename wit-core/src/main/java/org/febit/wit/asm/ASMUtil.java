// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.asm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;
import org.febit.wit.util.ClassUtil;
import org.febit.wit_shaded.asm.ClassWriter;
import org.febit.wit_shaded.asm.Constants;
import org.febit.wit_shaded.asm.MethodWriter;

/**
 *
 * @author zqq90
 */
class ASMUtil {

    static final String TYPE_STRING_NAME = "java/lang/String";
    static final String METHOD_CTOR = "<init>";
    static final AtomicInteger NEXT_SN = new AtomicInteger(1);
    private static final AsmClassLoader CLASS_LOADER = new AsmClassLoader();

    private ASMUtil() {
    }

    static Class loadClass(String name, ClassWriter classWriter) {
        return CLASS_LOADER.loadClass(name, classWriter.toByteArray());
    }

    static String getBoxedInternalName(Class type) {
        if (!type.isPrimitive()) {
            return ASMUtil.getInternalName(type.getName());
        }
        if (type == int.class) {
            return "java/lang/Integer";
        }
        if (type == boolean.class) {
            return "java/lang/Boolean";
        }
        if (type == long.class) {
            return "java/lang/Long";
        }
        if (type == double.class) {
            return "java/lang/Double";
        }
        if (type == float.class) {
            return "java/lang/Float";
        }
        if (type == short.class) {
            return "java/lang/Short";
        }
        if (type == char.class) {
            return "java/lang/Character";
        }
        if (type == byte.class) {
            return "java/lang/Byte";
        }
        // void.class
        return "java/lang/Void";
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

    static String getDescriptor(final Constructor c) {
        StringBuilder buf = new StringBuilder();
        buf.append('(');
        for (Class paramType : c.getParameterTypes()) {
            buf.append(getDescriptor(paramType));
        }
        return buf.append(")V").toString();
    }

    static String getDescriptor(final Method m) {
        StringBuilder buf = new StringBuilder();
        buf.append('(');
        for (Class paramType : m.getParameterTypes()) {
            buf.append(getDescriptor(paramType));
        }
        return buf.append(')').append(getDescriptor(m.getReturnType())).toString();
    }

    static String getDescriptor(Class c) {
        if (!c.isPrimitive()) {
            String internalName = getInternalName(c.getName());
            if (c.isArray()) {
                return internalName;
            }
            return new StringBuffer(internalName.length() + 2).append('L').append(internalName).append(';').toString();
        }
        if (c == int.class) {
            return "I";
        }
        if (c == boolean.class) {
            return "Z";
        }
        if (c == byte.class) {
            return "B";
        }
        if (c == char.class) {
            return "C";
        }
        if (c == short.class) {
            return "S";
        }
        if (c == double.class) {
            return "D";
        }
        if (c == float.class) {
            return "F";
        }
        if (c == long.class) {
            return "J";
        }
        // void.class
        return "V";
    }

    static void visitBoxIfNeed(final MethodWriter m, final Class type) {
        if (!type.isPrimitive()) {
            return;
        }
        if (type == void.class) {
            m.visitFieldInsn(Constants.GETSTATIC, "org/febit/wit/lang/InternalVoid", "VOID", "Lorg/febit/wit/lang/InternalVoid;");
            return;
        }
        String boxedType = getBoxedInternalName(type);
        m.invokeStatic(boxedType, "valueOf", "(" + getDescriptor(type) + ")L" + boxedType + ";");
    }

    static void visitUnboxIfNeed(final MethodWriter m, final Class type) {
        if (!type.isPrimitive()) {
            return;
        }
        if (type == void.class) {
            // ignore void.class
            return;
        }
        m.invokeVirtual(getBoxedInternalName(type), type.getName() + "Value", "()" + getDescriptor(type));
    }

    static void visitScriptRuntimeException(final MethodWriter m, final String message) {
        m.visitTypeInsn(Constants.NEW, "org/febit/wit/exceptions/ScriptRuntimeException");
        m.visitInsn(Constants.DUP);
        m.visitLdcInsn(message);
        m.visitMethodInsn(Constants.INVOKESPECIAL, "org/febit/wit/exceptions/ScriptRuntimeException", METHOD_CTOR, "(Ljava/lang/String;)V");
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
        protected Class findClass(String name) throws ClassNotFoundException {
            return ClassUtil.getDefaultClassLoader().loadClass(name);
        }

        Class loadClass(String name, byte[] b) throws ClassFormatError {
            return defineClass(name, b, 0, b.length, null);
        }
    }
}
