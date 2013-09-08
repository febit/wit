// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.asm;

import java.util.HashMap;
import java.util.Map;
import jodd.util.StringUtil;
import webit.script.asm4.Type;
import webit.script.asm4.commons.Method;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.util.ClassUtil;

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
    public static final Method METHOD_DEFAULT_CONSTRUCTOR = new Method("<init>", Type.VOID_TYPE, new Type[0]);
    //
    private static final Map<Class, Method> BOX_METHDO_MAP;
    private static final Map<Class, Method> UNBOX_METHDO_MAP;

    static {
        //XXX:
        Map<Class, Method> boxMap = new HashMap<Class, Method>(12, 0.75f);
        boxMap.put(boolean.class, new Method("box", ASMUtil.BOOLEAN_BOXED_TYPE, new Type[]{Type.BOOLEAN_TYPE}));
        boxMap.put(char.class, new Method("box", ASMUtil.CHAR_BOXED_TYPE, new Type[]{Type.CHAR_TYPE}));
        boxMap.put(byte.class, new Method("box", ASMUtil.BYTE_BOXED_TYPE, new Type[]{Type.BYTE_TYPE}));
        boxMap.put(short.class, new Method("box", ASMUtil.SHORT_BOXED_TYPE, new Type[]{Type.SHORT_TYPE}));
        boxMap.put(int.class, new Method("box", ASMUtil.INT_BOXED_TYPE, new Type[]{Type.INT_TYPE}));
        boxMap.put(long.class, new Method("box", ASMUtil.LONG_BOXED_TYPE, new Type[]{Type.LONG_TYPE}));
        boxMap.put(float.class, new Method("box", ASMUtil.FLOAT_BOXED_TYPE, new Type[]{Type.FLOAT_TYPE}));
        boxMap.put(double.class, new Method("box", ASMUtil.DOUBLE_BOXED_TYPE, new Type[]{Type.DOUBLE_TYPE}));
        BOX_METHDO_MAP = boxMap;

        Map<Class, Method> unBoxMap = new HashMap<Class, Method>(12, 0.75f);
        unBoxMap.put(boolean.class, new Method("unBox", Type.BOOLEAN_TYPE, new Type[]{ASMUtil.BOOLEAN_BOXED_TYPE}));
        unBoxMap.put(char.class, new Method("unBox", Type.CHAR_TYPE, new Type[]{ASMUtil.CHAR_BOXED_TYPE}));
        unBoxMap.put(byte.class, new Method("unBox", Type.BYTE_TYPE, new Type[]{ASMUtil.BYTE_BOXED_TYPE}));
        unBoxMap.put(short.class, new Method("unBox", Type.SHORT_TYPE, new Type[]{ASMUtil.SHORT_BOXED_TYPE}));
        unBoxMap.put(int.class, new Method("unBox", Type.INT_TYPE, new Type[]{ASMUtil.INT_BOXED_TYPE}));
        unBoxMap.put(long.class, new Method("unBox", Type.LONG_TYPE, new Type[]{ASMUtil.LONG_BOXED_TYPE}));
        unBoxMap.put(float.class, new Method("unBox", Type.FLOAT_TYPE, new Type[]{ASMUtil.FLOAT_BOXED_TYPE}));
        unBoxMap.put(double.class, new Method("unBox", Type.DOUBLE_TYPE, new Type[]{ASMUtil.DOUBLE_BOXED_TYPE}));
        UNBOX_METHDO_MAP = unBoxMap;
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
            return Thread.currentThread().getContextClassLoader().loadClass(name);
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
