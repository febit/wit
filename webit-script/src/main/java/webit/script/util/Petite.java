// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import webit.script.loggers.Logger;
import webit.script.util.bean.BeanUtil;
import webit.script.util.bean.BeanUtilException;

/**
 * A Simple IoC.
 *
 * @author zqq90
 */
public final class Petite {

    private final Map<String, Object> datas;
    private Map<String, Entry> entrys;
    private boolean initalized;
    private Logger logger;

    public Petite() {
        this.datas = new HashMap<String, Object>();
    }

    public static String resolveBeanName(Object bean) {
        return bean.getClass().getName();
    }

    public void wireBean(Object bean) {
        wireBean(resolveBeanName(bean), bean);
    }

    public void wireBean(String beanName, final Object bean) {
        initalize();
        wireBean(beanName, bean, new HashSet<String>());
    }

    private void wireBean(final String beanName, final Object bean, final Set<String> injected) {

        if (injected.contains(beanName)) {
            return;
        }
        injected.add(beanName);

        //inject @extends first
        for (String profile : StringUtil.toArray(String.valueOf(datas.get(beanName.concat(".@extends"))))) {
            wireBean(profile, bean, injected);
        }

        Entry entry = entrys.get(beanName);
        while (entry != null) {
            try {
                BeanUtil.set(bean, entry.name, entry.value, true);
            } catch (BeanUtilException ex) {
                if (logger != null) {
                    logger.warn("Failed to set declared property {}: {}", entry.name, ex);
                }
            }
            entry = entry.next;
        }
    }

    public void set(Props props, Map<String, Object> parameters) {
        if (props == null) {
            props = new Props();
        }
        final Map<String, Object> extras;
        if (parameters != null) {
            extras = new HashMap<String, Object>();
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                String key = entry.getKey();
                if (key == null) {
                    continue;
                }
                key = key.trim();
                Object value = entry.getValue();
                if (value instanceof String) {
                    int len = key.length();
                    if (len > 0) {
                        if (key.charAt(len - 1) == '+') {
                            props.append(key.substring(0, len - 1).trim(), (String) value);
                        } else {
                            props.set(key, (String) value);
                        }
                    }
                } else {
                    extras.put(key, value);
                }
            }
        } else {
            extras = null;
        }

        props.extractTo(this.datas);
        if (extras != null) {
            this.datas.putAll(extras);
        }
        initalized = false;
    }

    public void set(String name, Object value) {
        datas.put(name, value);
        initalized = false;
    }

    public Object get(String name) {
        return datas.get(name);
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    private void initalize() {
        if (initalized) {
            return;
        }
        final Map<String, Entry> paramEntrys = new HashMap<String, Entry>();
        for (Map.Entry<String, Object> entry : datas.entrySet()) {
            String key = entry.getKey();
            int index = key.lastIndexOf('.');
            int index2;
            if (index > 0
                    && (index2 = index + 1) < key.length()
                    && key.charAt(index2) != '@') {
                String beanName = key.substring(0, index);
                paramEntrys.put(beanName,
                        new Entry(
                                key.substring(index2),
                                entry.getValue(),
                                paramEntrys.get(beanName)));
            }
        }
        this.entrys = paramEntrys;
        this.initalized = true;
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
