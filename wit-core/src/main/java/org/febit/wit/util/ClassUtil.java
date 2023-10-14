// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import jakarta.annotation.Nullable;
import lombok.experimental.UtilityClass;
import org.febit.wit.exceptions.UncheckedException;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author zqq90
 */
@SuppressWarnings({
        "WeakerAccess"
})
@UtilityClass
public class ClassUtil {

    private static final ClassMap<Map<String, Method[]>> PUBLIC_MEMBER_METHODS_CACHE = new ClassMap<>();

    public static String getClassName(@Nullable Object object) {
        return object != null ? object.getClass().getName() : "null";
    }

    public static Map<String, Field> getSettableMemberFields(final Class<?> type) {
        var fields = new HashMap<String, Field>();
        Class<?> cls = type;
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

    public static Method[] getPublicMemberMethods(Class<?> type, String name) {
        Map<String, Method[]> map = PUBLIC_MEMBER_METHODS_CACHE.get(type);
        if (map == null) {
            map = PUBLIC_MEMBER_METHODS_CACHE.putIfAbsent(type, new HashMap<>());
        }
        return map.computeIfAbsent(name,
                n -> resolvePublicMemberMethods(type, n));
    }

    private static Method[] resolvePublicMemberMethods(Class<?> type, String name) {
        return getMethods(type,
                method -> !isPublic(method)
                        || isStatic(method)
                        || !method.getName().equals(name));
    }

    public static Method[] getPublicMethods(Class<?> type, String name) {
        return getMethods(type,
                method -> !isPublic(method)
                        || !method.getName().equals(name));
    }

    private static Method[] getMethods(Class<?> type, Predicate<Method> exclude) {
        Method[] allMethods = type.getMethods();
        Map<String, Method> result = new HashMap<>();
        for (Method method : allMethods) {
            if (exclude.test(method)) {
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
        //void.class
        return Void.class;
    }

    public static char getAliasOfBaseType(final String name) {
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

    public static Class<?> getClass(final String name, final int arrayDepth) throws ClassNotFoundException {
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

    @Nullable
    public static Class<?> getPrimitiveClass(@Nullable final String name) {
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

    public static Class<?> getClass(final String name) {
        try {
            Class<?> cls = getPrimitiveClass(name);
            return cls != null ? cls : getClassByInternalName(name);
        } catch (ClassNotFoundException ex) {
            throw new UncheckedException(ex);
        }
    }

    private static Class<?> getClassByInternalName(String name) throws ClassNotFoundException {
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

    /**
     * @deprecated unused
     */
    @Deprecated
    public static boolean isProtected(Member member) {
        return Modifier.isProtected(member.getModifiers());
    }

    public static boolean isVoidType(Class<?> cls) {
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

    public static Object newInstance(final Class<?> type) {
        try {
            return type.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
            throw new UncheckedException(ex);
        }
    }
}
