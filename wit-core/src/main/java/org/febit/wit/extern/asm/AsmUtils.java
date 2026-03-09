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

import static org.febit.wit.util.Defaults.nvl;

@UtilityClass
class AsmUtils {

    static final String TYPE_OBJ = "java/lang/Object";
    static final String TYPE_STRING = "java/lang/String";
    static final String TYPE_EVAL_EX = "org/febit/wit/exception/ScriptEvaluateException";
    static final String TYPE_UNDEFINED = "org/febit/wit/runtime/Undefined";

    static final String METHOD_CTOR = "<init>";
    static final AtomicLong SEQ = new AtomicLong(1);

    private static final AsmClassLoader CLASS_LOADER = new AsmClassLoader();

    static Class<?> loadClass(String name, ClassWriter classWriter) {
        return CLASS_LOADER.loadClass(name, classWriter.toByteArray());
    }

    static String toBoxedInternalName(Class<?> type) {
        var boxed = nvl(ClassUtils.toBoxed(type), type);
        return toInternalName(boxed.getName());
    }

    static String toInternalName(String className) {
        return className.replace('.', '/');
    }

    static String getDescriptor(Constructor<?> c) {
        var buf = new StringBuilder();
        buf.append('(');
        for (var paramType : c.getParameterTypes()) {
            buf.append(getDescriptor(paramType));
        }
        return buf.append(")V").toString();
    }

    static String getDescriptor(Method m) {
        var buf = new StringBuilder();
        buf.append('(');
        for (var paramType : m.getParameterTypes()) {
            buf.append(getDescriptor(paramType));
        }
        return buf.append(')').append(getDescriptor(m.getReturnType())).toString();
    }

    static String getDescriptor(Class<?> c) {
        if (c.isPrimitive()) {
            return String.valueOf(ClassUtils.getAliasOfBaseType(c.getName()));
        }
        var internalName = toInternalName(c.getName());
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
            m.visitFieldInsn(Constants.GETSTATIC, TYPE_UNDEFINED,
                    "UNDEFINED", "L" + TYPE_UNDEFINED + ";");
            return;
        }
        var boxedType = toBoxedInternalName(type);
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
        m.invokeVirtual(toBoxedInternalName(type), type.getName() + "Value", "()" + getDescriptor(type));
    }

    static void visitScriptEvaluateException(MethodWriter m, String message) {
        m.visitTypeInsn(Constants.NEW, TYPE_EVAL_EX);
        m.visitInsn(Constants.DUP);
        m.visitLdcInsn(message);
        m.visitMethodInsn(Constants.INVOKESPECIAL,
                TYPE_EVAL_EX,
                METHOD_CTOR, "(Ljava/lang/String;)V");
        m.visitInsn(Constants.ATHROW);
    }

    static void visitConstructor(ClassWriter classWriter) {
        var m = classWriter.visitMethod(Constants.ACC_PUBLIC, METHOD_CTOR, "()V", null);
        m.visitVarInsn(Constants.ALOAD, 0);
        m.visitMethodInsn(Constants.INVOKESPECIAL, TYPE_OBJ, METHOD_CTOR, "()V");
        m.visitInsn(Constants.RETURN);
        m.visitMaxs();
    }

    private static final class AsmClassLoader extends ClassLoader {

        AsmClassLoader() {
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            return ClassUtils.classLoader().loadClass(name);
        }

        Class<?> loadClass(String name, byte[] b) throws ClassFormatError {
            return defineClass(name, b, 0, b.length, null);
        }
    }
}
