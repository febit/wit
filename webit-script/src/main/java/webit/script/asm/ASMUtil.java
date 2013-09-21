// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.asm;

import java.util.HashMap;
import java.util.Map;
import webit.script.Context;
import webit.script.asm3.Type;
import webit.script.asm3.commons.Method;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.util.ClassLoaderUtil;
import webit.script.util.ClassUtil;
import webit.script.util.StringUtil;

/**
 *
 * @author Zqq
 */
public class ASMUtil {

    public static final Type BOOLEAN_BOXED_TYPE = Type.getType(Boolean.class);
    public static final Type CHAR_BOXED_TYPE = Type.getType(Character.class);
    public static final Type BYTE_BOXED_TYPE = Type.getType(Byte.class);
    public static final Type SHORT_BOXED_TYPE = Type.getType(Short.class);
    public static final Type INT_BOXED_TYPE = Type.getType(Integer.class);
    public static final Type LONG_BOXED_TYPE = Type.getType(Long.class);
    public static final Type FLOAT_BOXED_TYPE = Type.getType(Float.class);
    public static final Type DOUBLE_BOXED_TYPE = Type.getType(Double.class);
    public static final Type TYPE_CLASS_UTIL = Type.getType(ClassUtil.class);
    //
    public static final Type TYPE_STRING = Type.getType(String.class);
    public static final Type TYPE_OBJECT = Type.getType(Object.class);
    public static final Type TYPE_SCRIPT_RUNTIME_EXCEPTION = Type.getType(ScriptRuntimeException.class);
    //
    public static final Type TYPE_ASM_CALLER = Type.getType(AsmMethodCaller.class);
    public static final Type TYPE_CONTEXT = Type.getType(Context.class);
    public static final Type TYPE_SYSTEM = Type.getType(System.class);
    public static final Type TYPE_OBJECT_ARR = Type.getType(Object[].class);
    public static final Type TYPE_ASM_RESOLVER = Type.getType(AsmResolver.class);
    //
    public static final Method METHOD_HASH_CODE = new Method("hashCode", "()I");
    public static final Method METHOD_EQUALS = new Method("equals", "(Ljava/lang/Object;)Z");
    public static final Method METHOD_DEFAULT_CONSTRUCTOR = new Method("<init>", "()V");
    //
    public static final Method METHOD_ASM_RESOLVER_GET = new Method("get", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
    public static final Method METHOD_ASM_RESOLVER_SET = new Method("set", "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Z");
    public static final Method METHOD_CONSTRUCTOR_ASM_RESOLVER = new Method("<init>", "(Ljava/lang/Class;)V");
    public static final Method METHOD_CREATE_NOSUCHPROPERTY_EXCEPTION = new Method("createNoSuchPropertyException", "(Ljava/lang/Object;)Lwebit/script/exceptions/ScriptRuntimeException;");
    public static final Method METHOD_CREATE_UNWRITE_EXCEPTION = new Method("createUnwriteablePropertyException", "(Ljava/lang/Object;)Lwebit/script/exceptions/ScriptRuntimeException;");
    public static final Method METHOD_CREATE_UNREAD_EXCEPTION = new Method("createUnreadablePropertyException", "(Ljava/lang/Object;)Lwebit/script/exceptions/ScriptRuntimeException;");
    //AsmMethodCaller.
    public static final Method METHOD_CREATE_EXCEPTION = new Method("createException", "(Ljava/lang/String;)Lwebit/script/exceptions/ScriptRuntimeException;");
    //Object execute(Object[])
    public static final Method METHOD_EXECUTE = new Method("execute", "([Ljava/lang/Object;)Ljava/lang/Object;");
    //System.
    public static final Method METHOD_ARRAY_COPY = new Method("arraycopy", "(Ljava/lang/Object;ILjava/lang/Object;II)V");
    //
    private static final Map<Class, Method> BOX_METHDO_MAP;
    private static final Map<Class, Method> UNBOX_METHDO_MAP;

    static {
        //XXX:
        Map<Class, Method> boxMap = new HashMap<Class, Method>(12, 0.75f);
        boxMap.put(boolean.class, new Method("box", "(Z)Ljava/lang/Boolean;"));
        boxMap.put(char.class, new Method("box", "(C)Ljava/lang/Character;"));
        boxMap.put(byte.class, new Method("box", "(B)Ljava/lang/Byte;"));
        boxMap.put(short.class, new Method("box", "(S)Ljava/lang/Short;"));
        boxMap.put(int.class, new Method("box", "(I)Ljava/lang/Integer;"));
        boxMap.put(long.class, new Method("box", "(J)Ljava/lang/Long;"));
        boxMap.put(float.class, new Method("box", "(F)Ljava/lang/Float;"));
        boxMap.put(double.class, new Method("box", "(D)Ljava/lang/Double;"));
        BOX_METHDO_MAP = boxMap;

        Map<Class, Method> unBoxMap = new HashMap<Class, Method>(12, 0.75f);
        unBoxMap.put(boolean.class, new Method("unBox", "(Ljava/lang/Boolean;)Z"));
        unBoxMap.put(char.class, new Method("unBox", "(Ljava/lang/Character;)C"));
        unBoxMap.put(byte.class, new Method("unBox", "(Ljava/lang/Byte;)B"));
        unBoxMap.put(short.class, new Method("unBox", "(Ljava/lang/Short;)S"));
        unBoxMap.put(int.class, new Method("unBox", "(Ljava/lang/Integer;)I"));
        unBoxMap.put(long.class, new Method("unBox", "(Ljava/lang/Long;)J"));
        unBoxMap.put(float.class, new Method("unBox", "(Ljava/lang/Float;)F"));
        unBoxMap.put(double.class, new Method("unBox", "(Ljava/lang/Double;)D"));
        UNBOX_METHDO_MAP = unBoxMap;
    }
    private static int sn = 1;

    static synchronized int getSn() {
        return sn++;
    }

    public static Method getBoxMethod(Class type) {
        return BOX_METHDO_MAP.get(type);
    }

    public static Method getUnBoxMethod(Class type) {
        return UNBOX_METHDO_MAP.get(type);
    }

    private static class AsmClassLoader extends ClassLoader {

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            return ClassLoaderUtil.getDefaultClassLoader().loadClass(name);
        }

        public final Class<?> loadClass(String name, byte[] b, int off, int len)
                throws ClassFormatError {
            return defineClass(name, b, off, len, null);
        }
    };
    private static AsmClassLoader classLoader = new AsmClassLoader();

    public static Class loadClass(String name, byte[] b, int off, int len) {
        return classLoader.loadClass(name, b, off, len);
    }

    /**
     * "java.util.Object" to "java/util/Object"
     *
     * @param className
     * @return
     */
    public static String toAsmClassName(String className) {
        return StringUtil.replaceChar(className, '.', '/');
    }
}
