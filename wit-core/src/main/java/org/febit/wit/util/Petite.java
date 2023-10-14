// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import org.febit.wit.Init;
import org.febit.wit.exceptions.UncheckedException;
import org.febit.wit.lang.InternedEncoding;
import org.febit.wit.loggers.Logger;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * A simple component injection.
 *
 * @author zqq90
 * @since 2.0
 */
public class Petite {

    private final Map<Class, Object> components = new HashMap<>();
    private final Map<String, Object> beans = new HashMap<>();
    private final Map<String, Object> data = new HashMap<>();
    private final Map<String, Entry> entries = new HashMap<>();

    private Logger logger;

    /**
     * Get component or bean by type.
     *
     * @param <T>
     * @param type
     * @return Component
     */
    @SuppressWarnings("unchecked")
    public <T> T get(final Class<T> type) {
        T bean = (T) components.get(type);
        if (bean != null) {
            return bean;
        }
        return (T) get(type.getName());
    }

    /**
     * Get bean by name.
     *
     * @param name
     * @return bean
     */
    public Object get(final String name) {
        Object bean = this.beans.get(name);
        if (bean != null) {
            return bean;
        }
        return resolveBeanIfAbsent(name);
    }

    private synchronized Object resolveBeanIfAbsent(String key) {
        Object bean = this.beans.get(key);
        if (bean != null) {
            return bean;
        }
        bean = newInstance(key);
        inject(key, bean);
        this.beans.put(key, bean);
        return bean;
    }

    @SuppressWarnings({
            "squid:S135" // Loops should not contain more than a single "break" or "continue" statement
    })
    public void config(Props props, Map<String, Object> parameters) {
        if (props == null) {
            props = new Props();
        }
        Map<String, Object> extras = null;
        if (parameters != null) {
            parameters.remove(null);
            extras = new HashMap<>();
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                String key = entry.getKey().trim();
                if (key.isEmpty()) {
                    continue;
                }
                Object value = entry.getValue();
                if (!(value instanceof String)) {
                    extras.put(key, value);
                    continue;
                }
                int len = key.length();
                if (key.charAt(len - 1) == '+') {
                    props.append(key.substring(0, len - 1).trim(), (String) value);
                } else {
                    props.set(key, (String) value);
                }
            }
        }
        props.forEach(this::setProp);

        if (extras != null) {
            extras.forEach(this::setProp);
        }
    }

    public Object getConfig(String key) {
        return this.data.get(key);
    }

    private void setProp(String key, Object value) {
        this.data.put(key, value);
        int index = key.lastIndexOf('.');
        int index2 = index + 1;
        if (index > 0
                && index2 < key.length()
                && key.charAt(index2) != '@') {
            String beanName = key.substring(0, index);
            this.entries.put(beanName,
                    new Entry(key.substring(index2), value, this.entries.get(beanName)));
        }
    }

    public void addComponent(Object bean) {
        //register all impls
        Class<?> cls = bean.getClass();
        while (cls != null && cls != Object.class) {
            this.components.put(cls, bean);
            for (Class<?> aInterface : cls.getInterfaces()) {
                this.components.put(aInterface, bean);
            }
            cls = cls.getSuperclass();
        }
    }

    public void initComponents() {
        addComponent(this);

        final String[] beanNames = StringUtil.toArray((String) data.get("@global"));
        final int size = beanNames.length;
        final Object[] globalBeans = new Object[size];
        for (int i = 0; i < size; i++) {
            String name = beanNames[i];
            Object bean = newInstance(name);
            globalBeans[i] = bean;
            this.beans.put(name, bean);
            addComponent(bean);
        }
        for (int i = 0; i < size; i++) {
            inject(beanNames[i], globalBeans[i]);
        }
        this.logger = get(Logger.class);
    }

    private Object newInstance(String key) {
        String type;
        do {
            type = key;
            key = (String) this.data.get(key + ".@class");
        } while (key != null);
        return ClassUtil.newInstance(type);
    }

    public void inject(String key, final Object bean) {
        LinkedList<String> keys = new LinkedList<>();
        do {
            keys.addFirst(key);
            key = (String) this.data.get(key + ".@class");
        } while (key != null);

        Map<String, Field> fields = ClassUtil.getSettableMemberFields(bean.getClass());
        //global
        for (Field field : fields.values()) {
            Object comp = this.components.get(field.getType());
            if (comp == null) {
                continue;
            }
            try {
                field.set(bean, comp);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                //shouldn't be
                throw new UncheckedException(ex);
            }
        }

        Set<String> injected = new HashSet<>();
        for (String profile : keys) {
            inject(profile, bean, injected, fields);
        }

        invokeInitMethods(bean);
    }

    private void invokeInitMethods(Object bean) {
        for (Method method : bean.getClass().getMethods()) {
            if (method.getAnnotation(Init.class) == null) {
                continue;
            }
            final Class<?>[] argTypes = method.getParameterTypes();
            final Object[] args;
            if (argTypes.length == 0) {
                args = ArrayUtil.emptyObjects();
            } else {
                args = new Object[argTypes.length];
                for (int i = 0; i < argTypes.length; i++) {
                    args[i] = this.components.get(argTypes[i]);
                }
            }
            try {
                ClassUtil.setAccessible(method);
                method.invoke(bean, args);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                //shouldn't be
                throw new UncheckedException(ex);
            }
        }
    }

    private void inject(final String beanName, final Object bean,
                        final Set<String> injected, final Map<String, Field> fields) {

        if (injected.contains(beanName)) {
            return;
        }
        injected.add(beanName);

        //inject @extends first
        for (String profile : StringUtil.toArray(
                (String) data.get(beanName.concat(".@extends")))) {
            inject(profile, bean, injected, fields);
        }

        for (Entry entry = entries.get(beanName); entry != null; entry = entry.next) {
            Field field = fields.get(entry.name);
            if (field == null) {
                if (logger != null) {
                    logger.warn("Not found field {}#{} ", bean.getClass(), entry.name);
                }
                continue;
            }
            Object value = entry.value;
            if (value instanceof String) {
                value = convert((String) value, field.getType());
            }
            try {
                field.set(bean, value);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                throw new UncheckedException(ex);
            }
        }
    }

    @SuppressWarnings({
            "squid:S3776" // Cognitive Complexity of methods should not be too high
    })
    private Object convert(String string, Class<?> cls) {
        if (cls == String.class) {
            return string;
        }
        if (cls == int.class) {
            if (string == null || string.isEmpty()) {
                return 0;
            }
            return Integer.parseInt(string);
        }
        if (cls == boolean.class) {
            return Boolean.valueOf(string);
        }
        if (string == null) {
            return null;
        }
        if (cls.isArray()) {
            final String[] strings = StringUtil.toArray(string);
            if (cls == String[].class) {
                return strings;
            }
            final Class<?> componentType = cls.getComponentType();
            final int len = strings.length;
            final Object array = Array.newInstance(componentType, len);
            for (int i = 0; i < len; i++) {
                Array.set(array, i, convert(strings[i], componentType));
            }
            return array;
        }
        if (cls == Boolean.class) {
            return Boolean.valueOf(string);
        }
        if (string.isEmpty()) {
            return null;
        }
        if (cls == Class.class) {
            return ClassUtil.getClass(string);
        }
        if (cls == Integer.class) {
            return Integer.parseInt(string);
        }
        if (cls == InternedEncoding.class) {
            return InternedEncoding.intern(string);
        }
        return get(string);
    }

    private static final class Entry {

        final String name;
        final Object value;
        final Entry next;

        Entry(String name, Object value, Entry next) {
            this.name = name;
            this.value = value;
            this.next = next;
        }
    }
}
