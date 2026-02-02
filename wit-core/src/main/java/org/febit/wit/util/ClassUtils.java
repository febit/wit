// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import lombok.experimental.UtilityClass;
import org.febit.wit.exceptions.UncheckedException;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

@UtilityClass
public class ClassUtils {

    private static final ClassMap<Map<String, Method[]>> PUBLIC_MEMBER_METHODS_CACHE = new ClassMap<>();

    public static String name(@Nullable Object object) {
        return object != null ? object.getClass().getName() : "null";
    }

    public static Method[] getPublicMemberMethods(Class<?> type, String name) {
        var map = PUBLIC_MEMBER_METHODS_CACHE.get(type);
        if (map == null) {
            map = PUBLIC_MEMBER_METHODS_CACHE.putIfAbsent(type, new HashMap<>());
        }
        return map.computeIfAbsent(name,
                n -> resolvePublicMemberMethods(type, n));
    }

    private static Method[] resolvePublicMemberMethods(Class<?> type, String name) {
        return getMethods(type, method ->
                !isPublic(method)
                        || isStatic(method)
                        || !method.getName().equals(name));
    }

    public static Method[] getPublicMethods(Class<?> type, String name) {
        return getMethods(type, method ->
                !isPublic(method)
                        || !method.getName().equals(name));
    }

    private static Method[] getMethods(Class<?> type, Predicate<Method> exclude) {
        Method[] allMethods = type.getMethods();
        Map<String, Method> result = new HashMap<>();
        for (Method method : allMethods) {
            if (exclude.test(method)) {
                continue;
            }
            var keyBuf = new StringBuilder();
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
        var methods = result.values()
                .toArray(new Method[0]);
        setAccessible(methods);
        return methods;
    }

    public static ClassLoader getDefaultClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    @Nullable
    public static Class<?> getBoxedPrimitiveClass(Class<?> type) {
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
        if (type == void.class) {
            return Void.class;
        }
        throw new IllegalStateException("Unknown primitive type: " + type.getName());
    }

    public static char getAliasOfBaseType(final String name) {
        return switch (name) {
            case "int" -> 'I';
            case "long" -> 'J';
            case "short" -> 'S';
            case "boolean" -> 'Z';
            case "char" -> 'C';
            case "double" -> 'D';
            case "float" -> 'F';
            case "byte" -> 'B';
            case "void" -> 'V';
            default -> '\0';
        };
    }

    public static Class<?> loadByName(String name, int arrayDepth) throws ClassNotFoundException {
        if (arrayDepth == 0) {
            return loadByName(name);
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
        return loadByQualifiedName(new String(chars));
    }

    @Nullable
    public static Class<?> findPrimitiveClass(@Nullable final String name) {
        if (name == null) {
            return null;
        }
        return switch (name) {
            case "int" -> int.class;
            case "long" -> long.class;
            case "short" -> short.class;
            case "boolean" -> boolean.class;
            case "char" -> char.class;
            case "double" -> double.class;
            case "float" -> float.class;
            case "byte" -> byte.class;
            case "void" -> void.class;
            default -> null;
        };
    }

    public static Class<?> loadByName(String name) {
        try {
            var cls = findPrimitiveClass(name);
            return cls != null ? cls
                    : loadByQualifiedName(name);
        } catch (ClassNotFoundException ex) {
            throw new UncheckedException(ex);
        }
    }

    private static Class<?> loadByQualifiedName(String name) throws ClassNotFoundException {
        return Class.forName(name, true, getDefaultClassLoader());
    }

    public static boolean isStatic(Member member) {
        return Modifier.isStatic(member.getModifiers());
    }

    public static boolean isFinal(Member member) {
        return Modifier.isFinal(member.getModifiers());
    }

    @SuppressWarnings({
            "BooleanMethodIsAlwaysInverted"
    })
    public static boolean isPublic(Class<?> cls) {
        return Modifier.isPublic(cls.getModifiers());
    }

    @SuppressWarnings({
            "BooleanMethodIsAlwaysInverted"
    })
    public static boolean isPublic(Member member) {
        return Modifier.isPublic(member.getModifiers());
    }

    public static boolean isVoidType(Class<?> cls) {
        return cls == void.class || cls == Void.class;
    }

    public static <T extends AccessibleObject> void setAccessible(T[] objects) {
        for (T obj : objects) {
            setAccessible(obj);
        }
    }

    public static void setAccessible(AccessibleObject obj) {
        if (obj.isAccessible()) {
            return;
        }
        try {
            obj.setAccessible(true);
        } catch (SecurityException ignore) {
            // Ignore
        }
    }
}
