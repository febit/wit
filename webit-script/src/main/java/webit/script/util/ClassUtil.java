// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Zqq
 */
public class ClassUtil {

    public static Class<?> getBoxedClass(final Class<?> type) {
        if (type.isPrimitive()) {
            if (type == int.class) {
                return Integer.class;
            } else if (type == boolean.class) {
                return Boolean.class;
            } else if (type == long.class) {
                return Long.class;
            } else if (type == double.class) {
                return Double.class;
            } else if (type == float.class) {
                return Float.class;
            } else if (type == short.class) {
                return Short.class;
            } else if (type == char.class) {
                return Character.class;
            } else if (type == byte.class) {
                return Byte.class;
            } else /* if (type == void.class) */ {
                return Void.class;
            }
        } else {
            return type;
        }
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
    private static final Map<String, Class<?>> CLASS_CACHE;

    static {

        Map<String, Class<?>> classes = new HashMap<String, Class<?>>(32, 0.75f); //24*4/3
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

    public static boolean isStatic(Member member) {
        return Modifier.isStatic(member.getModifiers());
    }

    public static boolean isStatic(Method method) {
        return Modifier.isStatic(method.getModifiers());
    }

    public static boolean isPublic(Class cls) {
        return Modifier.isPublic(cls.getModifiers());
    }

    public static boolean isPublic(Constructor member) {
        return Modifier.isPublic(member.getModifiers());
    }

    public static boolean isPublic(Method member) {
        return Modifier.isPublic(member.getModifiers());
    }

    public static boolean isPublic(Member member) {
        return Modifier.isPublic(member.getModifiers());
    }

    public static void setAccessible(AccessibleObject accessible) {
        try {
            if (!accessible.isAccessible()) {
                accessible.setAccessible(true);
            }
        } catch (SecurityException ignore) {
        }
    }
    public static Method searchMethod(Class<?> currentClass, String name, Class<?>[] parameterTypes, boolean boxed) throws NoSuchMethodException {
        return currentClass.getMethod(name, parameterTypes);
    }
}
