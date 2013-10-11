// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.asm;

import webit.script.asm3.ClassWriter;
import webit.script.asm3.Opcodes;
import webit.script.asm3.Type;
import webit.script.asm3.commons.GeneratorAdapter;
import webit.script.asm3.commons.Method;
import webit.script.util.ClassLoaderUtil;
import webit.script.util.StringUtil;

/**
 *
 * @author Zqq
 */
public class ASMUtil {

    //
    public static final String ASM_CLASS_OBJECT = "java/lang/Object";
    //
    public static final Type TYPE_STRING = Type.getObjectType("java/lang/String");
    public static final Type TYPE_OBJECT = Type.getObjectType("java/lang/Object");
    //
    public static final Type TYPE_CONTEXT = Type.getObjectType("webit/script/Context");
    public static final Type TYPE_SYSTEM = Type.getObjectType("java/lang/System");
    public static final Type TYPE_OBJECT_ARR = Type.getObjectType("[Ljava/lang/Object;");
    public static final Type TYPE_SCRIPT_RUNTIME_EXCEPTION = Type.getObjectType("webit/script/exceptions/ScriptRuntimeException");
    public static final Type TYPE_MATCH_MODE = Type.getObjectType("webit/script/resolvers/MatchMode");
    //
    public static final Method METHOD_HASH_CODE = new Method("hashCode", "()I");
    public static final Method METHOD_EQUALS = new Method("equals", "(Ljava/lang/Object;)Z");
    public static final Method METHOD_DEFAULT_CONSTRUCTOR = new Method("<init>", "()V");
    public static final Method METHOD_TO_STRING = new Method("toString", "()Ljava/lang/String;");
    //String
    public static final Method METHOD_CONCAT = new Method("concat", "(Ljava/lang/String;)Ljava/lang/String;");
    //
    public static final Method METHOD_CONSTRUCTOR_SCRIPT_RUNTIME_EXCEPTION = new Method("<init>", "(Ljava/lang/String;)V");
    //
    public static final Method METHOD_ASM_RESOLVER_getMatchClass = new Method("getMatchClass", "()Ljava/lang/Class;");
    public static final Method METHOD_ASM_RESOLVER_getMatchMode = new Method("getMatchMode", "()Lwebit/script/resolvers/MatchMode;");
    public static final Method METHOD_ASM_RESOLVER_GET = new Method("get", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
    public static final Method METHOD_ASM_RESOLVER_SET = new Method("set", "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Z");
    //AsmMethodCaller Object execute(Object[])
    public static final Method METHOD_EXECUTE = new Method("execute", "([Ljava/lang/Object;)Ljava/lang/Object;");
    //System.
    public static final Method METHOD_ARRAY_COPY = new Method("arraycopy", "(Ljava/lang/Object;ILjava/lang/Object;II)V");
    //
    private static final Method METHOD_BOOLEAN_VALUE = new Method("booleanValue", "()Z");
    private static final Method METHOD_CHAR_VALUE = new Method("charValue", "()C");
    private static final Method METHOD_BYTE_VALUE = new Method("byteValue", "()B");
    private static final Method METHOD_SHORT_VALUE = new Method("shortValue", "()S");
    private static final Method METHOD_INT_VALUE = new Method("intValue", "()I");
    private static final Method METHOD_LONG_VALUE = new Method("longValue", "()J");
    private static final Method METHOD_FLOAT_VALUE = new Method("floatValue", "()F");
    private static final Method METHOD_DOUBLE_VALUE = new Method("doubleValue", "()D");
    //
    private static final Method METHOD_BOOLEAN_VALUE_OF = new Method("valueOf", "(Z)Ljava/lang/Boolean;");
    private static final Method METHOD_CHAR_VALUE_OF = new Method("valueOf", "(C)Ljava/lang/Character;");
    private static final Method METHOD_BYTE_VALUE_OF = new Method("valueOf", "(B)Ljava/lang/Byte;");
    private static final Method METHOD_SHORT_VALUE_OF = new Method("valueOf", "(S)Ljava/lang/Short;");
    private static final Method METHOD_INT_VALUE_OF = new Method("valueOf", "(I)Ljava/lang/Integer;");
    private static final Method METHOD_LONG_VALUE_OF = new Method("valueOf", "(J)Ljava/lang/Long;");
    private static final Method METHOD_FLOAT_VALUE_OF = new Method("valueOf", "(F)Ljava/lang/Float;");
    private static final Method METHOD_DOUBLE_VALUE_OF = new Method("valueOf", "(D)Ljava/lang/Double;");
    //
    private static final Type BYTE_TYPE = Type.getObjectType("java/lang/Byte");
    private static final Type BOOLEAN_TYPE = Type.getObjectType("java/lang/Boolean");
    private static final Type SHORT_TYPE = Type.getObjectType("java/lang/Short");
    private static final Type CHARACTER_TYPE = Type.getObjectType("java/lang/Character");
    private static final Type INTEGER_TYPE = Type.getObjectType("java/lang/Integer");
    private static final Type FLOAT_TYPE = Type.getObjectType("java/lang/Float");
    private static final Type LONG_TYPE = Type.getObjectType("java/lang/Long");
    private static final Type DOUBLE_TYPE = Type.getObjectType("java/lang/Double");
    private static final Type VOID_TYPE = Type.getObjectType("java/lang/Void");
    //

    private static int sn = 1;

    static synchronized int getSn() {
        return sn++;
    }

    private final static class AsmClassLoader extends ClassLoader {

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            return ClassLoaderUtil.getDefaultClassLoader().loadClass(name);
        }

        Class<?> loadClass(String name, byte[] b, int off, int len)
                throws ClassFormatError {
            return defineClass(name, b, off, len, null);
        }
    };
    private static AsmClassLoader classLoader = new AsmClassLoader();

    public static Class loadClass(String name, byte[] b) {
        return classLoader.loadClass(name, b, 0, b.length);
    }

    /**
     * "java.util.Object" to "java/util/Object"
     *
     * @param className
     * @return class name can be uesd in asm
     */
    public static String toAsmClassName(String className) {
        return StringUtil.replaceChar(className, '.', '/');
    }

    public static Type getBoxedType(Class<?> type) {
        if (type.isPrimitive()) {
            if (type == int.class) {
                return INTEGER_TYPE;
            } else if (type == boolean.class) {
                return BOOLEAN_TYPE;
            } else if (type == long.class) {
                return LONG_TYPE;
            } else if (type == double.class) {
                return DOUBLE_TYPE;
            } else if (type == float.class) {
                return FLOAT_TYPE;
            } else if (type == short.class) {
                return SHORT_TYPE;
            } else if (type == char.class) {
                return CHARACTER_TYPE;
            } else if (type == byte.class) {
                return BYTE_TYPE;
            } else /* if (type == void.class) */ {
                return VOID_TYPE;
            }
        } else {
            return Type.getType(type);
        }
    }

    public static void attachBoxCodeIfNeed(final GeneratorAdapter mg, final Class type) {
        if (type.isPrimitive()) {
            if (type == int.class) {
                mg.invokeStatic(INTEGER_TYPE, METHOD_INT_VALUE_OF);
            } else if (type == boolean.class) {
                mg.invokeStatic(BOOLEAN_TYPE, METHOD_BOOLEAN_VALUE_OF);
            } else if (type == long.class) {
                mg.invokeStatic(LONG_TYPE, METHOD_LONG_VALUE_OF);
            } else if (type == double.class) {
                mg.invokeStatic(DOUBLE_TYPE, METHOD_DOUBLE_VALUE_OF);
            } else if (type == float.class) {
                mg.invokeStatic(FLOAT_TYPE, METHOD_FLOAT_VALUE_OF);
            } else if (type == short.class) {
                mg.invokeStatic(SHORT_TYPE, METHOD_SHORT_VALUE_OF);
            } else if (type == char.class) {
                mg.invokeStatic(CHARACTER_TYPE, METHOD_CHAR_VALUE_OF);
            } else if (type == byte.class) {
                mg.invokeStatic(BYTE_TYPE, METHOD_BYTE_VALUE_OF);
            } else /* if (type == void.class) */ {
                mg.getStatic(ASMUtil.TYPE_CONTEXT, "VOID", ASMUtil.TYPE_OBJECT);
            }
        }// else ignore
    }

    public static void attachUnBoxCodeIfNeed(final GeneratorAdapter mg, final Class type) {
        if (type.isPrimitive()) {
            if (type == int.class) {
                mg.invokeVirtual(INTEGER_TYPE, METHOD_INT_VALUE);
            } else if (type == boolean.class) {
                mg.invokeVirtual(BOOLEAN_TYPE, METHOD_BOOLEAN_VALUE);
            } else if (type == long.class) {
                mg.invokeVirtual(LONG_TYPE, METHOD_LONG_VALUE);
            } else if (type == double.class) {
                mg.invokeVirtual(DOUBLE_TYPE, METHOD_DOUBLE_VALUE);
            } else if (type == float.class) {
                mg.invokeVirtual(FLOAT_TYPE, METHOD_FLOAT_VALUE);
            } else if (type == short.class) {
                mg.invokeVirtual(SHORT_TYPE, METHOD_SHORT_VALUE);
            } else if (type == char.class) {
                mg.invokeVirtual(CHARACTER_TYPE, METHOD_CHAR_VALUE);
            } else if (type == byte.class) {
                mg.invokeVirtual(BYTE_TYPE, METHOD_BYTE_VALUE);
            } else /* if (type == void.class) */ {
                //ignore
            }
        }// else ignore
    }

    public static void attachThrowScriptRuntimeException(final GeneratorAdapter mg, final String message) {
        mg.newInstance(TYPE_SCRIPT_RUNTIME_EXCEPTION);
        mg.dup();
        mg.push(message);
        mg.invokeConstructor(TYPE_SCRIPT_RUNTIME_EXCEPTION, METHOD_CONSTRUCTOR_SCRIPT_RUNTIME_EXCEPTION);
        mg.throwException();
    }

    public static void attachDefaultConstructorMethod(ClassWriter classWriter) {
        final GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, ASMUtil.METHOD_DEFAULT_CONSTRUCTOR, null, null, classWriter);
        mg.loadThis();
        mg.invokeConstructor(ASMUtil.TYPE_OBJECT, ASMUtil.METHOD_DEFAULT_CONSTRUCTOR);
        mg.returnValue();
        mg.endMethod();
    }
}
