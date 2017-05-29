// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author zqq90
 */
public class ClassUtil {

    public static final ClassMap<Map<String, Method[]>> PUBLIC_MEMBER_METHODS_CACHE = new ClassMap<>();

    private ClassUtil() {
    }

    public static List<Class> classes(Class cls) {
        List<Class> classes = new ArrayList<>();
        while (cls != null && cls != Object.class) {
            classes.add(cls);
            cls = cls.getSuperclass();
        }
        return classes;
    }

    public static List<Class> impls(Class cls) {
        List<Class> classes = new ArrayList<>();
        Set<Class> interfaces = new HashSet<>();
        while (cls != null && cls != Object.class) {
            classes.add(cls);
            interfaces.addAll(Arrays.asList(cls.getInterfaces()));
            cls = cls.getSuperclass();
        }
        classes.addAll(interfaces);
        return classes;
    }

    public static Map<String, Field> getSetableMemberFields(Class type) {
        final Map<String, Field> fields = new HashMap<>();
        for (Class cls : classes(type)) {
            for (Field field : cls.getDeclaredFields()) {
                int modifiers = field.getModifiers();
                if (!Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers)
                        && (field.getDeclaringClass() == type
                        || Modifier.isPublic(modifiers)
                        || Modifier.isProtected(modifiers))) {
                    setAccessible(field);
                    fields.put(field.getName(), field);
                }
            }
        }
        return fields;
    }

    public static Method[] getPublicMemberMethods(Class type, String name) {
        Map<String, Method[]> map = PUBLIC_MEMBER_METHODS_CACHE.get(type);
        if (map == null) {
            map = PUBLIC_MEMBER_METHODS_CACHE.putIfAbsent(type, new HashMap<String, Method[]>());
        }
        Method[] result = map.get(name);
        if (result == null) {
            result = _getPublicMemberMethods(type, name);
            map.put(name, result);
        }
        return result;
    }

    protected static Method[] _getPublicMemberMethods(Class type, String name) {
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
        return result.values().toArray(new Method[result.size()]);
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
            throw new RuntimeException(ex);
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

    public static boolean isVoidType(Class cls) {
        return cls == void.class || cls == Void.class;
    }

    public static void setAccessible(AccessibleObject accessible) {
        if (accessible.isAccessible()) {
            return;
        }
        try {
            accessible.setAccessible(true);
        } catch (SecurityException ignore) {
            // ignore
        }
    }

    public static Object newInstance(final String type) {
        return newInstance(getClass(type));
    }

    public static Object newInstance(final Class type) {
        try {
            return type.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }
}
