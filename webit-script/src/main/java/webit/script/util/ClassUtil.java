// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
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

    public static ClassLoader getDefaultClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    private static char getAliasOfBaseType(final String name) {
        //fast check if under root package and start with lower(except 'a')
        if (name.charAt(0) > 'a' && name.indexOf('.', 1) < 0) {
            if ("int".equals(name)) {
                return 'I';
            }
            if ("long".equals(name)) {
                return 'J';
            }
            if ("short".equals(name)) {
                return 'S';
            }
            if ("boolean".equals(name)) {
                return 'Z';
            }
            if ("char".equals(name)) {
                return 'C';
            }
            if ("double".equals(name)) {
                return 'D';
            }
            if ("float".equals(name)) {
                return 'F';
            }
            if ("byte".equals(name)) {
                return 'B';
            }
            if ("void".equals(name)) {
                return 'V';
            }
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
        if (name != null && name.length() != 0 && name.charAt(0) > 'a' && name.indexOf('.', 1) < 0) {

            if (name.equals("int")) {
                return int.class;
            }
            if (name.equals("long")) {
                return long.class;
            }
            if (name.equals("short")) {
                return short.class;
            }
            if (name.equals("boolean")) {
                return boolean.class;
            }
            if (name.equals("char")) {
                return char.class;
            }
            if (name.equals("double")) {
                return double.class;
            }
            if (name.equals("float")) {
                return float.class;
            }
            if (name.equals("byte")) {
                return byte.class;
            }
            if (name.equals("void")) {
                return void.class;
            }
        }
        return null;
    }

    public static Class getClass(final String name) {
        try {
            Class cls;
            return (cls = getPrimitiveClass(name)) != null ? cls : getClassByInternalName(name);
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

    public static void setAccessible(AccessibleObject accessible) {
        if (!accessible.isAccessible()) {
            try {
                accessible.setAccessible(true);
            } catch (SecurityException ignore) {
            }
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
