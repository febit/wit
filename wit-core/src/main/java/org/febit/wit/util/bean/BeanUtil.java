// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.util.bean;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.febit.wit.util.ClassMap;
import org.febit.wit.util.ClassUtil;
import org.febit.wit.util.StringUtil;

/**
 *
 * @author zqq90
 */
public class BeanUtil {

    private static final ClassMap<Map<String, Accessor>> CACHE = new ClassMap<>();

    private BeanUtil() {
    }

    public static Object get(final Object bean, final String name) throws BeanException {
        Getter getter = getAccessor(bean.getClass(), name).getter;
        if (getter != null) {
            return getter.get(bean);
        }
        throw new BeanException(StringUtil.format("Unable to get getter for {}#{}", bean.getClass(), name));
    }

    public static void set(final Object bean, final String name, Object value) throws BeanException {
        Setter setter = getAccessor(bean.getClass(), name).setter;
        if (setter != null) {
            setter.set(bean, value);
            return;
        }
        throw new BeanException(StringUtil.format("Unable to get setter for {}#{}", bean.getClass(), name));
    }

    private static Accessor getAccessor(final Class cls, final String name) throws BeanException {
        Map<String, Accessor> descs = CACHE.unsafeGet(cls);
        if (descs == null) {
            descs = CACHE.putIfAbsent(cls, resolveAccessors(cls));
        }
        Accessor fieldDescriptor = descs.get(name);
        if (fieldDescriptor != null) {
            return fieldDescriptor;
        }
        throw new BeanException(StringUtil.format("Unable to get field: {}#{}", cls.getName(), name));
    }

    private static Map<String, Accessor> resolveAccessors(Class cls) {
        final FieldInfo[] fieldInfos = FieldInfoResolver.resolve(cls);
        final Map<String, Accessor> map = new HashMap<>(fieldInfos.length * 4 / 3 + 1, 0.75f);
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

    private static final class Accessor {

        final Getter getter;
        final Setter setter;

        Accessor(Getter getter, Setter setter) {
            this.getter = getter;
            this.setter = setter;
        }
    }

    private abstract static class Getter {

        Getter() {
        }

        public abstract Object get(Object bean);
    }

    private abstract static class Setter {

        Setter() {
        }

        public abstract Class getType();

        public abstract void set(Object bean, Object value);
    }

    private static final class MethodGetter extends Getter {

        private final Method method;

        MethodGetter(Method method) {
            ClassUtil.setAccessible(method);
            this.method = method;
        }

        @Override
        public Object get(Object bean) throws BeanException {
            try {
                return this.method.invoke(bean, (Object[]) null);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                throw new BeanException(ex.toString(), ex);
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
        public Class getType() {
            return this.fieldType;
        }

        @Override
        public void set(Object bean, Object value) throws BeanException {
            try {
                this.method.invoke(bean, new Object[]{value});
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                throw new BeanException(ex.toString(), ex);
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
        public Object get(Object bean) throws BeanException {
            try {
                return this.field.get(bean);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                throw new BeanException(ex.toString(), ex);
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
        public void set(Object bean, Object value) throws BeanException {
            try {
                this.field.set(bean, value);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                throw new BeanException(ex.toString(), ex);
            }
        }

        @Override
        public Class getType() {
            return this.field.getType();
        }
    }
}
