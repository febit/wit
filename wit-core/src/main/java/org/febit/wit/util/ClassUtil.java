// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.febit.wit.exceptions.UncheckedException;

/**
 *
 * @author zqq90
 */
public class ClassUtil {

    private static final ClassMap<Map<String, Method[]>> PUBLIC_MEMBER_METHODS_CACHE = new ClassMap<>();

    private ClassUtil() {
    }

    public static Map<String, Field> getSettableMemberFields(final Class type) {
        final Map<String, Field> fields = new HashMap<>();
        Class cls = type;
        while (cls != null && cls != Object.class) {
            for (Field f : cls.getDeclaredFields()) {
                int mod = f.getModifiers();
                if (Modifier.isStatic(mod)
                        || Modifier.isFinal(mod)
                        || !(Modifier.isPublic(mod) || Modifier.isProtected(mod))) {
                    continue;
                }
                setAccessible(f);
                fields.put(f.getName(), f);
            }
            cls = cls.getSuperclass();
        }
        return fields;
    }

    public static Method[] getPublicMemberMethods(Class type, String name) {
        Map<String, Method[]> map = PUBLIC_MEMBER_METHODS_CACHE.get(type);
        if (map == null) {
            map = PUBLIC_MEMBER_METHODS_CACHE.putIfAbsent(type, new HashMap<>());
        }
        Method[] result = map.get(name);
        if (result == null) {
            result = resolvePublicMemberMethods(type, name);
            map.put(name, result);
        }
        return result;
    }

    private static Method[] resolvePublicMemberMethods(Class type, String name) {
        Method[] allMethods = type.getMethods();
        Map<String, Method> result = new HashMap<>();
        for (Method method : allMethods) {
            if (!isPublic(method)
                    || isStatic(method)
                    || !method.getName().equals(name)) {
                continue;
            }
            StringBuilder keyBuf = new StringBuilder();
            for (Class<?> parameterType : method.getParameterTypes()) {
                keyBuf.append(parameterType.getName())
                        .append(',');
            }
            String key = keyBuf.toString();
            Method old = result.get(key);
            if (old == null
                    || old.getDeclaringClass()
                            .isAssignableFrom(method.getDeclaringClass())) {
                result.put(key, method);
            }
        }
        Method[] methods = result.values().toArray(new Method[result.size()]);
        setAccessible(methods);
        return methods;
    }

    /**
     * XXX: without cache
     *
     * @param type
     * @param name
     * @return
     */
    public static Method[] getPublicMethods(Class type, String name) {
        Method[] allMethods = type.getMethods();
        Map<String, Method> result = new HashMap<>();
        for (Method method : allMethods) {
            if (!isPublic(method)
                    || !method.getName().equals(name)) {
                continue;
            }
            StringBuilder keyBuf = new StringBuilder();
            for (Class<?> parameterType : method.getParameterTypes()) {
                keyBuf.append(parameterType.getName())
                        .append(',');
            }
            String key = keyBuf.toString();
            Method old = result.get(key);
            if (old == null
                    || old.getDeclaringClass()
                            .isAssignableFrom(method.getDeclaringClass())) {
                result.put(key, method);
            }
        }
        Method[] methods = result.values().toArray(new Method[result.size()]);
        setAccessible(methods);
        return methods;
    }

    public static ClassLoader getDefaultClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static Class getBoxedPrimitiveClass(Class type) {
        if (!type.isPrimitive()) {
            return null;
        }
        if (type == int.class) {
            return Integer.class;
        }
        if (type == boolean.class) {
            return Boolean.class;
        }
        if (type == long.class) {
            return Long.class;
        }
        if (type == double.class) {
            return Double.class;
        }
        if (type == float.class) {
            return Float.class;
        }
        if (type == short.class) {
            return Short.class;
        }
        if (type == char.class) {
            return Character.class;
        }
        if (type == byte.class) {
            return Byte.class;
        }
        //void.class
        return Void.class;
    }

    private static char getAliasOfBaseType(final String name) {
        switch (name) {
            case "int":
                return 'I';
            case "long":
                return 'J';
            case "short":
                return 'S';
            case "boolean":
                return 'Z';
            case "char":
                return 'C';
            case "double":
                return 'D';
            case "float":
                return 'F';
            case "byte":
                return 'B';
            case "void":
                return 'V';
            default:
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
            Arrays.fill(chars, 0, arrayDepth, '[');
            chars[arrayDepth] = 'L';
            name.getChars(0, name.length(), chars, arrayDepth + 1);
            chars[chars.length - 1] = ';';
        } else {
            chars = new char[arrayDepth + 1];
            Arrays.fill(chars, 0, arrayDepth, '[');
            chars[arrayDepth] = alias;
        }
        return getClassByInternalName(new String(chars));
    }

    public static Class getPrimitiveClass(final String name) {
        if (name == null) {
            return null;
        }
        switch (name) {
            case "int":
                return int.class;
            case "long":
                return long.class;
            case "short":
                return short.class;
            case "boolean":
                return boolean.class;
            case "char":
                return char.class;
            case "double":
                return double.class;
            case "float":
                return float.class;
            case "byte":
                return byte.class;
            case "void":
                return void.class;
            default:
                return null;
        }
    }

    public static Class getClass(final String name) {
        try {
            Class cls = getPrimitiveClass(name);
            return cls != null ? cls : getClassByInternalName(name);
        } catch (ClassNotFoundException ex) {
            throw new UncheckedException(ex);
        }
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

    public static boolean isProtected(Member member) {
        return Modifier.isProtected(member.getModifiers());
    }

    public static boolean isVoidType(Class cls) {
        return cls == void.class || cls == Void.class;
    }

    public static <T extends AccessibleObject> void setAccessible(T[] accessibles) {
        for (T accessible : accessibles) {
            setAccessible(accessible);
        }
    }

    public static void setAccessible(AccessibleObject accessible) {
        if (accessible.isAccessible()) {
            return;
        }
        try {
            accessible.setAccessible(true);
        } catch (SecurityException ex) {
            ExceptionUtil.ignore(ex);
        }
    }

    public static Object newInstance(final String type) {
        return newInstance(getClass(type));
    }

    public static Object newInstance(final Class type) {
        try {
            return type.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new UncheckedException(ex);
        }
    }
}
