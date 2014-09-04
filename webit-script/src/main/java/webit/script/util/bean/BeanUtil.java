// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import webit.script.util.ClassEntry;
import webit.script.util.ClassMap;
import webit.script.util.ClassUtil;
import webit.script.util.StringUtil;

/**
 *
 * @author Zqq
 */
public class BeanUtil {

    private static final ClassMap<Map<String, Accessor>> CACHE = new ClassMap<Map<String, Accessor>>();

    public static Object get(final Object bean, final String name) throws BeanUtilException {
        Getter getter;
        if ((getter = getAccessor(bean.getClass(), name).getter) != null) {
            return getter.get(bean);
        }
        throw new BeanUtilException(StringUtil.format("Unable to get getter for {}#{}", bean.getClass(), name));
    }

    public static void set(final Object bean, final String name, Object value) throws BeanUtilException {
        set(bean, name, value, false);
    }

    public static void set(final Object bean, final String name, Object value, boolean convertIfNeed) throws BeanUtilException {
        Setter setter;
        if ((setter = getAccessor(bean.getClass(), name).setter) != null) {
            if (convertIfNeed && (value == null || value instanceof String)) {
                value = convert((String) value, setter.getType());
            }
            setter.set(bean, value);
            return;
        }
        throw new BeanUtilException(StringUtil.format("Unable to get setter for {}#{}", bean.getClass(), name));
    }

    private static Accessor getAccessor(final Class cls, final String name) throws BeanUtilException {

        Map<String, Accessor> descs;
        if ((descs = CACHE.unsafeGet(cls)) == null) {
            descs = CACHE.putIfAbsent(cls, resolveAccessors(cls));
        }

        Accessor fieldDescriptor;
        if ((fieldDescriptor = descs.get(name)) != null) {
            return fieldDescriptor;
        }
        throw new BeanUtilException(StringUtil.format("Unable to get field: {}#{}", cls.getName(), name));
    }

    private static Map<String, Accessor> resolveAccessors(Class cls) {
        final FieldInfo[] fieldInfos = FieldInfoResolver.resolve(cls);
        final Map<String, Accessor> map = new HashMap<String, Accessor>(fieldInfos.length * 4 / 3 + 1, 0.75f);
        for (FieldInfo fieldInfo : fieldInfos) {

            map.put(fieldInfo.name, new Accessor(
                    fieldInfo.getGetter() != null ? new MethodGetter(fieldInfo.getGetter())
                    : fieldInfo.getField() != null ? new FieldGetter(fieldInfo.getField())
                    : null,
                    fieldInfo.getSetter() != null ? new MethodSetter(fieldInfo.getSetter())
                    : fieldInfo.isFieldSettable() ? new FieldSetter(fieldInfo.getField())
                    : null));
        }
        return map;
    }

    private static Object convert(String string, Class cls) {
        if (cls == String.class) {
            return string;
        }
        if (cls == int.class) {
            if (string == null || string.length() == 0) {
                return 0;
            }
            return toInt(string);
        }
        if (cls == boolean.class) {
            return toBoolean(string);
        }
        if (string == null) {
            return null;
        }
        if (cls.isArray()) {
            if (cls == Class[].class) {
                return toClassArray(string);
            }
            if (cls == ClassEntry[].class) {
                return toClassEntryArray(string);
            }
            if (cls == String[].class) {
                return toStringArray(string);
            }
            if (cls == int[].class) {
                return toIntArray(string);
            }
            if (cls == Integer[].class) {
                return toIntegerArray(string);
            }
            if (cls == boolean[].class) {
                return toBoolArray(string);
            }
            if (cls == Boolean[].class) {
                return toBooleanArray(string);
            }
        } else {
            if (cls == Boolean.class) {
                return toBoolean(string);
            }
            if (string.length() == 0) {
                return null;
            }
            if (cls == Class.class) {
                return toClass(string);
            }
            if (cls == ClassEntry.class) {
                return toClassEntry(string);
            }
            if (cls == Integer.class) {
                return toInt(string);
            }
        }
        return string;
    }

    private static Class toClass(String string) {
        try {
            return ClassUtil.getClass(string);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static boolean toBoolean(String string) {
        return string != null && string.equalsIgnoreCase("true");
    }

    private static int toInt(String string) {
        return Integer.valueOf(string);
    }

    private static ClassEntry toClassEntry(String string) {
        try {
            return ClassEntry.wrap(string);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static String[] toStringArray(String string) {
        if (string == null) {
            return null;
        }
        return StringUtil.toArray(string);
    }

    private static Class[] toClassArray(String string) {
        final String[] strings = StringUtil.toArray(string);
        final int len = strings.length;
        final Class[] entrys = new Class[len];
        for (int i = 0; i < len; i++) {
            entrys[i] = toClass(strings[i]);
        }
        return entrys;
    }

    private static boolean[] toBoolArray(String string) {
        final String[] strings = StringUtil.toArray(string);
        final int len = strings.length;
        final boolean[] entrys = new boolean[len];
        for (int i = 0; i < len; i++) {
            entrys[i] = toBoolean(strings[i]);
        }
        return entrys;
    }

    private static Boolean[] toBooleanArray(String string) {
        final String[] strings = StringUtil.toArray(string);
        final int len = strings.length;
        final Boolean[] entrys = new Boolean[len];
        for (int i = 0; i < len; i++) {
            entrys[i] = toBoolean(strings[i]);
        }
        return entrys;
    }

    private static int[] toIntArray(String string) {
        final String[] strings = StringUtil.toArray(string);
        final int len = strings.length;
        final int[] entrys = new int[len];
        for (int i = 0; i < len; i++) {
            entrys[i] = toInt(strings[i]);
        }
        return entrys;
    }

    private static Integer[] toIntegerArray(String string) {
        final String[] strings = StringUtil.toArray(string);
        final int len = strings.length;
        final Integer[] entrys = new Integer[len];
        for (int i = 0; i < len; i++) {
            entrys[i] = toInt(strings[i]);
        }
        return entrys;
    }

    private static ClassEntry[] toClassEntryArray(final String string) {
        final String[] strings = StringUtil.toArray(string);
        final int len = strings.length;
        final ClassEntry[] entrys = new ClassEntry[len];
        for (int i = 0; i < len; i++) {
            entrys[i] = toClassEntry(strings[i]);
        }
        return entrys;
    }

    private static final class Accessor {

        final Getter getter;
        final Setter setter;

        Accessor(Getter getter, Setter setter) {
            this.getter = getter;
            this.setter = setter;
        }
    }

    private static abstract class Getter {

        Getter() {
        }

        abstract Object get(Object bean);
    }

    private static abstract class Setter {

        Setter() {
        }

        abstract Class getType();

        abstract void set(Object bean, Object value);
    }

    private static final class MethodGetter extends Getter {

        private final Method method;

        MethodGetter(Method method) {
            ClassUtil.setAccessible(method);
            this.method = method;
        }

        @Override
        Object get(Object bean) throws BeanUtilException {
            try {
                return this.method.invoke(bean, (Object[]) null);
            } catch (Exception ex) {
                throw new BeanUtilException(ex.toString());
            }
        }
    }

    private static final class MethodSetter extends Setter {

        private final Method method;
        private final Class fieldType;

        MethodSetter(Method method) {
            ClassUtil.setAccessible(method);
            this.method = method;
            this.fieldType = method.getParameterTypes()[0];
        }

        @Override
        Class getType() {
            return this.fieldType;
        }

        @Override
        void set(Object bean, Object value) throws BeanUtilException {
            try {
                this.method.invoke(bean, new Object[]{value});
            } catch (Exception ex) {
                throw new BeanUtilException(ex.toString());
            }
        }
    }

    private static final class FieldGetter extends Getter {

        private final Field field;

        FieldGetter(Field field) {
            ClassUtil.setAccessible(field);
            this.field = field;
        }

        @Override
        Object get(Object bean) throws BeanUtilException {
            try {
                return this.field.get(bean);
            } catch (Exception ex) {
                throw new BeanUtilException(ex.toString());
            }
        }
    }

    private static final class FieldSetter extends Setter {

        private final Field field;

        FieldSetter(Field field) {
            ClassUtil.setAccessible(field);
            this.field = field;
        }

        @Override
        void set(Object bean, Object value) throws BeanUtilException {
            try {
                this.field.set(bean, value);
            } catch (Exception ex) {
                throw new BeanUtilException(ex.toString());
            }
        }

        @Override
        Class getType() {
            return this.field.getType();
        }
    }
}
