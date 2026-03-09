// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import lombok.experimental.UtilityClass;
import org.febit.wit.exception.UncheckedException;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

@UtilityClass
public class ClassUtils {

    public static String className(@Nullable Object targetObj) {
        return targetObj != null ? targetObj.getClass().getName() : "null";
    }

    public static Stream<Method> methods(Class<?> target, String name) {
        var result = new HashMap<String, Method>();
        for (var method : target.getMethods()) {
            if (!method.getName().equals(name)) {
                continue;
            }
            var keyBuf = new StringBuilder();
            for (var type : method.getParameterTypes()) {
                keyBuf.append(type.getName())
                        .append(',');
            }
            var key = keyBuf.toString();
            var old = result.get(key);
            if (old == null
                    || old.getDeclaringClass()
                    .isAssignableFrom(method.getDeclaringClass())) {
                result.put(key, method);
            }
        }
        return result.values().stream();
    }

    public static ClassLoader classLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    @Nullable
    public static Class<?> toBoxed(Class<?> type) {
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
        return Class.forName(name, true, classLoader());
    }

    public static boolean isStatic(Member member) {
        return Modifier.isStatic(member.getModifiers());
    }

    public static boolean isNotStatic(Member member) {
        return !isStatic(member);
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
            obj.trySetAccessible();
        }
    }
}
