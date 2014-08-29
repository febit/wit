// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.asm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import webit.script.asm.lib.ClassWriter;
import webit.script.asm.lib.Constants;
import webit.script.asm.lib.MethodWriter;
import webit.script.util.ClassUtil;

/**
 *
 * @author Zqq
 */
class ASMUtil {

    private static int sn = 1;
    private static final AsmClassLoader CLASS_LOADER = new AsmClassLoader();

    static synchronized String getSn() {
        return Integer.toString(sn++);
    }

    static Class loadClass(String name, ClassWriter classWriter) {
        return CLASS_LOADER.loadClass(name, classWriter.toByteArray());
    }

    static String getBoxedInternalName(Class type) {
        if (type.isPrimitive()) {
            if (type == int.class) {
                return "java/lang/Integer";
            } else if (type == boolean.class) {
                return "java/lang/Boolean";
            } else if (type == long.class) {
                return "java/lang/Long";
            } else if (type == double.class) {
                return "java/lang/Double";
            } else if (type == float.class) {
                return "java/lang/Float";
            } else if (type == short.class) {
                return "java/lang/Short";
            } else if (type == char.class) {
                return "java/lang/Character";
            } else if (type == byte.class) {
                return "java/lang/Byte";
            } else {
                //void.class
                return "java/lang/Void";
            }
        } else {
            return ASMUtil.getInternalName(type);
        }
    }

    static String getInternalName(final Class c) {
        return getInternalName(c.getName());
    }

    static String getInternalName(String className) {
        int i;
        if ((i = className.indexOf('.')) < 0) {
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
        if (c.isPrimitive()) {
            if (c == int.class) {
                return "I";
            } else if (c == boolean.class) {
                return "Z";
            } else if (c == byte.class) {
                return "B";
            } else if (c == char.class) {
                return "C";
            } else if (c == short.class) {
                return "S";
            } else if (c == double.class) {
                return "D";
            } else if (c == float.class) {
                return "F";
            } else if (c == long.class) {
                return "J";
            } else {
                //Void.TYPE
                return "V";
            }
        }
        String internalName = getInternalName(c.getName());
        if (c.isArray()) {
            return internalName;
        }
        return new StringBuffer(internalName.length() + 2).append('L').append(internalName).append(';').toString();
    }

    static void visitBoxIfNeed(final MethodWriter m, final Class type) {
        if (type.isPrimitive()) {
            if (type == int.class) {
                m.invokeStatic("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
            } else if (type == boolean.class) {
                m.invokeStatic("java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
            } else if (type == long.class) {
                m.invokeStatic("java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
            } else if (type == double.class) {
                m.invokeStatic("java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
            } else if (type == float.class) {
                m.invokeStatic("java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
            } else if (type == short.class) {
                m.invokeStatic("java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
            } else if (type == char.class) {
                m.invokeStatic("java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
            } else if (type == byte.class) {
                m.invokeStatic("java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
            } else {
                //void.class
                m.visitFieldInsn(Constants.GETSTATIC, "webit/script/Context", "VOID", "Lwebit/script/lang/Void;");
            }
        }
    }

    static void visitUnboxIfNeed(final MethodWriter m, final Class type) {
        if (type.isPrimitive()) {
            if (type == int.class) {
                m.invokeVirtual("java/lang/Integer", "intValue", "()I");
            } else if (type == boolean.class) {
                m.invokeVirtual("java/lang/Boolean", "booleanValue", "()Z");
            } else if (type == long.class) {
                m.invokeVirtual("java/lang/Long", "longValue", "()J");
            } else if (type == double.class) {
                m.invokeVirtual("java/lang/Double", "doubleValue", "()D");
            } else if (type == float.class) {
                m.invokeVirtual("java/lang/Float", "floatValue", "()F");
            } else if (type == short.class) {
                m.invokeVirtual("java/lang/Short", "shortValue", "()S");
            } else if (type == char.class) {
                m.invokeVirtual("java/lang/Character", "charValue", "()C");
            } else if (type == byte.class) {
                m.invokeVirtual("java/lang/Byte", "byteValue", "()B");
            }
            //ignore void.class
        }
    }

    static void visitScriptRuntimeException(final MethodWriter m, final String message) {
        m.visitTypeInsn(Constants.NEW, "webit/script/exceptions/ScriptRuntimeException");
        m.visitInsn(Constants.DUP);
        m.visitLdcInsn(message);
        m.visitMethodInsn(Constants.INVOKESPECIAL, "webit/script/exceptions/ScriptRuntimeException", "<init>", "(Ljava/lang/String;)V");
        m.visitInsn(Constants.ATHROW);
    }

    static void visitConstructor(ClassWriter classWriter) {
        MethodWriter m = classWriter.visitMethod(Constants.ACC_PUBLIC, "<init>", "()V", null);
        m.visitVarInsn(Constants.ALOAD, 0);
        m.visitMethodInsn(Constants.INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
        m.visitInsn(Constants.RETURN);
        m.visitMaxs();
    }

    private static final class AsmClassLoader extends ClassLoader {

        AsmClassLoader() {
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            return ClassUtil.getDefaultClassLoader().loadClass(name);
        }

        Class<?> loadClass(String name, byte[] b) throws ClassFormatError {
            return defineClass(name, b, 0, b.length, null);
        }
    };
}
