// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

/**
 *
 * @author Zqq
 */
public class ClassUtil {

    public static ClassLoader getDefaultClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    private ClassUtil() {
    }

    public static char getAliasOfBaseType(final String name) {
        switch (name.charAt(0)) {
            case 'v':
                if ("void".equals(name)) {
                    return 'V';
                }
                break;
            case 'b':
                if ("boolean".equals(name)) {
                    return 'Z';
                }
                if ("byte".equals(name)) {
                    return 'B';
                }
                break;
            case 'c':
                if ("char".equals(name)) {
                    return 'C';
                }
                break;
            case 'd':
                if ("double".equals(name)) {
                    return 'D';
                }
                break;
            case 'f':
                if ("float".equals(name)) {
                    return 'F';
                }
                break;
            case 'i':
                if ("int".equals(name)) {
                    return 'I';
                }
                break;
            case 'l':
                if ("long".equals(name)) {
                    return 'J';
                }
                break;
            case 's':
                if ("short".equals(name)) {
                    return 'S';
                }
                break;
        }
        return '\0';
    }

    public static Class getClass(final String name, final int arrayDepth) throws ClassNotFoundException {

        if (arrayDepth == 0) {
            return getClass(name);
        }
        char alias = getAliasOfBaseType(name);
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

    public static Class getCachedClass(final String name) {
        if (name == null || name.length() == 0) {
            return null;
        }
        switch (name.charAt(0)) {
            case 'b':
                if (name.equals("boolean")) {
                    return boolean.class;
                }
                if (name.equals("byte")) {
                    return byte.class;
                }
            case 'c':
                if (name.equals("char")) {
                    return char.class;
                }
            case 's':
                if (name.equals("short")) {
                    return short.class;
                }
            case 'i':
                if (name.equals("int")) {
                    return int.class;
                }
            case 'l':
                if (name.equals("long")) {
                    return long.class;
                }
            case 'f':
                if (name.equals("float")) {
                    return float.class;
                }
            case 'd':
                if (name.equals("double")) {
                    return double.class;
                }
            case 'v':
                if (name.equals("void")) {
                    return void.class;
                }
            case 'B':
                if (name.equals("Boolean")) {
                    return Boolean.class;
                }
                if (name.equals("Byte")) {
                    return Byte.class;
                }
            case 'C':
                if (name.equals("Character")) {
                    return Character.class;
                }
                if (name.equals("Class")) {
                    return Class.class;
                }
            case 'S':
                if (name.equals("String")) {
                    return String.class;
                }
                if (name.equals("Short")) {
                    return Short.class;
                }
            case 'I':
                if (name.equals("Integer")) {
                    return Integer.class;
                }
            case 'L':
                if (name.equals("Long")) {
                    return Long.class;
                }
            case 'F':
                if (name.equals("Float")) {
                    return Float.class;
                }
            case 'D':
                if (name.equals("Double")) {
                    return Double.class;
                }
            case 'N':
                if (name.equals("Number")) {
                    return Number.class;
                }
            case 'O':
                if (name.equals("Object")) {
                    return Object.class;
                }
            case 'V':
                if (name.equals("Void")) {
                    return Void.class;
                }
        }
        return null;
    }

    public static Class getClass(final String name) throws ClassNotFoundException {
        Class cls;
        return (cls = getCachedClass(name)) != null ? cls : getClassByInternalName(name);
    }

    private static Class getClassByInternalName(String name) throws ClassNotFoundException {
        return Class.forName(name, true, getDefaultClassLoader());
    }

    public static boolean isStatic(Member member) {
        return Modifier.isStatic(member.getModifiers());
    }

    public static boolean isFinal(Member member) {
        return Modifier.isFinal(member.getModifiers());
    }

    public static boolean isPublic(Class cls) {
        return Modifier.isPublic(cls.getModifiers());
    }

    public static boolean isPublic(Member member) {
        return Modifier.isPublic(member.getModifiers());
    }

    public static void setAccessible(AccessibleObject accessible) {
        if (!accessible.isAccessible()) {
            try {
                accessible.setAccessible(true);
            } catch (SecurityException ignore) {
            }
        }
    }

    public static Object newInstance(final Class type) {
        try {
            return type.newInstance();
        } catch (InstantiationException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }
}
