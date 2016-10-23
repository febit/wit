// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import org.febit.wit.Init;
import org.febit.wit.loggers.Logger;

/**
 * A simple dependency injection.
 *
 * @author zqq90
 * @since 2.0
 */
public class Petite {

    private final Map<Class, Object> components = new HashMap<>();
    private final Map<String, Object> beans = new HashMap<>();
    private final Map<String, Object> datas = new HashMap<>();
    private final Map<String, Entry> entrys = new HashMap<>();

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

    public void config(Props props, Map<String, Object> parameters) {
        if (props == null) {
            props = new Props();
        }
        final Map<String, Object> extras;
        if (parameters != null) {
            extras = new HashMap<>();
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                String key = entry.getKey();
                if (key == null) {
                    continue;
                }
                key = key.trim();
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
        } else {
            extras = null;
        }

        for (String key : props.keySet()) {
            setProp(key, props.get(key));
        }

        if (extras != null) {
            for (Map.Entry<String, Object> entrySet : extras.entrySet()) {
                setProp(entrySet.getKey(), entrySet.getValue());
            }
        }
    }

    public Object getConfig(String key) {
        return this.datas.get(key);
    }

    private void setProp(String key, Object value) {
        this.datas.put(key, value);
        int index = key.lastIndexOf('.');
        int index2 = index + 1;
        if (index > 0
                && index2 < key.length()
                && key.charAt(index2) != '@') {
            String beanName = key.substring(0, index);
            this.entrys.put(beanName,
                    new Entry(key.substring(index2), value, this.entrys.get(beanName)));
        }
    }

    public void addComponent(Object bean) {
        //regist all impls
        for (Class cls : ClassUtil.impls(bean.getClass())) {
            this.components.put(cls, bean);
        }
    }

    public void initComponents() {
        addComponent(this);

        final String[] beanNames = StringUtil.toArray((String) datas.get("@global"));
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
            key = (String) this.datas.get(key + ".@class");
        } while (key != null);
        return ClassUtil.newInstance(type);
    }

    public void inject(String key, final Object bean) {
        LinkedList<String> keys = new LinkedList<>();
        do {
            keys.addFirst(key);
            key = (String) this.datas.get(key + ".@class");
        } while (key != null);

        Map<String, Field> fields = ClassUtil.getSetableMemberFields(bean.getClass());
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
                throw new RuntimeException(ex);
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
            final Class[] argTypes = method.getParameterTypes();
            final Object[] args;
            if (argTypes.length == 0) {
                args = ArrayUtil.EMPTY_OBJECTS;
            } else {
                args = new Object[argTypes.length];
                for (int i = 0; i < argTypes.length; i++) {
                    args[i] = this.components.get(argTypes[i]);
                }
            }
            try {
                method.invoke(bean, args);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                //shouldn't be
                throw new RuntimeException(ex);
            }
        }
    }

    private void inject(final String beanName, final Object bean, final Set<String> injected, final Map<String, Field> fields) {

        if (injected.contains(beanName)) {
            return;
        }
        injected.add(beanName);

        //inject @extends first
        for (String profile : StringUtil.toArray(
                (String) datas.get(beanName.concat(".@extends")))) {
            inject(profile, bean, injected, fields);
        }

        for (Entry entry = entrys.get(beanName); entry != null; entry = entry.next) {
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
                throw new RuntimeException(ex);
            }
        }
    }

    private Object convert(String string, Class cls) {
        if (cls == String.class) {
            return string;
        }
        if (cls == int.class) {
            if (string == null || string.length() == 0) {
                return 0;
            }
            return Integer.valueOf(string);
        }
        if (cls == boolean.class) {
            return Boolean.valueOf(string);
        }
        if (string == null) {
            return null;
        }
        if (cls.isArray()) {
            final String[] strings = StringUtil.toArray(string);
            final int len = strings.length;
            if (cls == String[].class) {
                return strings;
            }
            if (cls == Class[].class) {
                final Class[] entrys = new Class[len];
                for (int i = 0; i < len; i++) {
                    entrys[i] = ClassUtil.getClass(strings[i]);
                }
                return entrys;
            }
            if (cls == int[].class) {
                final int[] entrys = new int[len];
                for (int i = 0; i < len; i++) {
                    entrys[i] = Integer.valueOf(strings[i]);
                }
                return entrys;
            }
            if (cls == Integer[].class) {
                final Integer[] entrys = new Integer[len];
                for (int i = 0; i < len; i++) {
                    entrys[i] = Integer.valueOf(strings[i]);
                }
                return entrys;
            }
            if (cls == boolean[].class) {
                final boolean[] entrys = new boolean[len];
                for (int i = 0; i < len; i++) {
                    entrys[i] = Boolean.valueOf(strings[i]);
                }
                return entrys;
            }
            if (cls == Boolean[].class) {
                final Boolean[] entrys = new Boolean[len];
                for (int i = 0; i < len; i++) {
                    entrys[i] = Boolean.valueOf(strings[i]);
                }
                return entrys;
            }
            Object[] array = (Object[]) Array.newInstance(cls.getComponentType(), len);
            for (int i = 0; i < len; i++) {
                array[i] = get(strings[i]);
            }
            return array;
        } else {
            if (cls == Boolean.class) {
                return Boolean.valueOf(string);
            }
            if (string.length() == 0) {
                return null;
            }
            if (cls == Class.class) {
                return ClassUtil.getClass(string);
            }
            if (cls == Integer.class) {
                return Integer.valueOf(string);
            }
            if (cls == InternedEncoding.class) {
                return InternedEncoding.intern(string);
            }
            return get(string);
        }
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
