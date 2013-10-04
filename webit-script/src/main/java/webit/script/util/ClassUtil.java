// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 *
 * @author Zqq
 */
public class ClassUtil {

    public static boolean unBox(Boolean b) {
        return b.booleanValue();
    }

    public static char unBox(Character b) {
        return b.charValue();
    }

    public static byte unBox(Byte b) {
        return b.byteValue();
    }

    public static short unBox(Short b) {
        return b.shortValue();
    }

    public static int unBox(Integer b) {
        return b.intValue();
    }

    public static long unBox(Long b) {
        return b.longValue();
    }

    public static float unBox(Float b) {
        return b.floatValue();
    }

    public static double unBox(Double b) {
        return b.doubleValue();
    }

    public static Boolean box(boolean b) {
        return b ? Boolean.TRUE : Boolean.FALSE;
    }

    public static Character box(char c) {
        return Character.valueOf(c);
    }

    public static Byte box(byte c) {
        return Byte.valueOf(c);
    }

    public static Short box(short c) {
        return Short.valueOf(c);
    }

    public static Integer box(int c) {
        return Integer.valueOf(c);
    }

    public static Long box(long c) {
        return Long.valueOf(c);
    }

    public static Float box(float c) {
        return Float.valueOf(c);
    }

    public static Double box(double c) {
        return Double.valueOf(c);
    }

    public static Class<?> getBoxedClass(Class<?> type) {
        Class boxed;
        return (boxed = BOX_MAP.get(type)) != null ? boxed : type;
    }

    public static char getAliasOfBaseType(final String name) {
        switch (name.hashCode()) {
            case 3625364:
                if ("void" == name || "void".equals(name)) {
                    return 'V';
                }
                break;
            case 64711720:
                if ("boolean" == name || "boolean".equals(name)) {
                    return 'Z';
                }
                break;
            case 3039496:
                if ("byte" == name || "byte".equals(name)) {
                    return 'B';
                }
                break;
            case 3052374:
                if ("char" == name || "char".equals(name)) {
                    return 'C';
                }
                break;
            case -1325958191:
                if ("double" == name || "double".equals(name)) {
                    return 'D';
                }
                break;
            case 97526364:
                if ("float" == name || "float".equals(name)) {
                    return 'F';
                }
                break;
            case 104431:
                if ("int" == name || "int".equals(name)) {
                    return 'I';
                }
                break;
            case 3327612:
                if ("long" == name || "long".equals(name)) {
                    return 'J';
                }
                break;
            case 109413500:
                if ("short" == name || "short".equals(name)) {
                    return 'S';
                }
                break;
        }
        return '\0';
    }

    public static Class<?> getClass(final String name, final int arrayDepth) throws ClassNotFoundException {

        if (arrayDepth == 0) {
            return getClass(name);
        }
        char alias = getAliasOfBaseType(name);
        //final StringBuilder sb;
        final char[] chars;
        if (alias == '\0') {
            chars = new char[name.length() + 2 + arrayDepth];
            int i = arrayDepth - 1;
            while (i >= 0) {
                chars[i--] = '[';
            }
            chars[arrayDepth] = 'L';
            name.getChars(0, name.length(), chars, arrayDepth + 1);
            chars[chars.length - 1] = ';';
        } else {
            chars = new char[arrayDepth + 1];
            int i = arrayDepth - 1;
            while (i >= 0) {
                chars[i--] = '[';
            }
            chars[arrayDepth] = alias;
        }
        return getClassByInternalName(new String(chars));
    }
    private static final Map<Class, Class> BOX_MAP;
    private static final Map<String, Class<?>> CLASS_CACHE;

    static {

        Map<Class, Class> boxMap = new IdentityHashMap<Class, Class>(8);
        boxMap.put(boolean.class, Boolean.class);
        boxMap.put(char.class, Character.class);
        boxMap.put(byte.class, Byte.class);
        boxMap.put(short.class, Short.class);
        boxMap.put(int.class, Integer.class);
        boxMap.put(long.class, Long.class);
        boxMap.put(float.class, Float.class);
        boxMap.put(double.class, Double.class);
        BOX_MAP = boxMap;

        Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
        classes.put("boolean", boolean.class);
        classes.put("char", char.class);
        classes.put("byte", byte.class);
        classes.put("short", short.class);
        classes.put("int", int.class);
        classes.put("long", long.class);
        classes.put("float", float.class);
        classes.put("double", double.class);
        classes.put("void", void.class);
        classes.put("Boolean", Boolean.class);
        classes.put("Character", Character.class);
        classes.put("Byte", Byte.class);
        classes.put("Short", Short.class);
        classes.put("Integer", Integer.class);
        classes.put("Long", Long.class);
        classes.put("Float", Float.class);
        classes.put("Double", Double.class);
        classes.put("Number", Number.class);
        classes.put("String", String.class);
        classes.put("Object", Object.class);
        classes.put("Class", Class.class);
        classes.put("Void", Void.class);

        CLASS_CACHE = classes;
    }

    public static Class<?> getCachedClass(final String name) {
        return CLASS_CACHE.get(name);
    }

    public static Class<?> getClass(final String name) throws ClassNotFoundException {
        Class cls;
        return (cls = CLASS_CACHE.get(name)) != null ? cls : getClassByInternalName(name);
    }

    private static Class<?> getClassByInternalName(String name) throws ClassNotFoundException {
        return Class.forName(name, true, ClassLoaderUtil.getDefaultClassLoader());
    }

    public static boolean isStatic(Method method) {
        return Modifier.isStatic(method.getModifiers());
    }

    public static boolean isPublic(Class cls) {
        return Modifier.isPublic(cls.getModifiers());
    }

    //XXX: rewrite
    public static Method searchMethod(Class<?> currentClass, String name, Class<?>[] parameterTypes, boolean boxed) throws NoSuchMethodException {
        if (currentClass == null) {
            throw new NoSuchMethodException("class == null");
        }
        try {
            return currentClass.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            Method likeMethod = null;
            for (Method method : currentClass.getMethods()) {
                if (method.getName().equals(name)
                        && parameterTypes.length == method.getParameterTypes().length
                        && Modifier.isPublic(method.getModifiers())) {
                    if (parameterTypes.length > 0) {
                        Class<?>[] types = method.getParameterTypes();
                        boolean eq = true;
                        boolean like = true;
                        for (int i = 0; i < parameterTypes.length; i++) {
                            Class<?> type = types[i];
                            Class<?> parameterType = parameterTypes[i];
                            if (type != null && parameterType != null
                                    && !type.equals(parameterType)) {
                                eq = false;
                                if (boxed) {
                                    type = ClassUtil.getBoxedClass(type);
                                    parameterType = ClassUtil.getBoxedClass(parameterType);
                                }
                                if (!type.isAssignableFrom(parameterType)) {
                                    eq = false;
                                    like = false;
                                    break;
                                }
                            }
                        }
                        if (!eq) {
                            if (like && (likeMethod == null || likeMethod.getParameterTypes()[0]
                                    .isAssignableFrom(method.getParameterTypes()[0]))) {
                                likeMethod = method;
                            }
                            continue;
                        }
                    }
                    return method;
                }
            }
            if (likeMethod != null) {
                return likeMethod;
            }
            throw e;
        }
    }
}
