// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.global;

import org.febit.wit.Init;
import org.febit.wit.lang.Bag;
import org.febit.wit.lang.MethodDeclare;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;

/**
 * @author zqq90
 */
public class GlobalManager {

    private final ConcurrentMap<String, Object> constVars;
    private final ConcurrentMap<String, Object> globalVars;

    //settings
    protected GlobalRegister[] registers;

    public GlobalManager() {
        this.constVars = new ConcurrentHashMap<>();
        this.globalVars = new ConcurrentHashMap<>();
    }

    @Init
    public void init() {
        if (registers != null) {
            for (GlobalRegister register : registers) {
                register.regist(this);
            }
        }
    }

    public void clear() {
        this.constVars.clear();
        this.globalVars.clear();
        init();
    }

    /**
     * Performs the given action for each const vars until all have been processed or the action throws an exception.
     *
     * @param action
     * @since 2.5.0
     */
    public void forEachConst(BiConsumer<String, Object> action) {
        Objects.requireNonNull(action);
        this.constVars.forEach(action);
    }

    /**
     * Performs the given action for each global vars until all have been processed or the action throws an exception.
     *
     * @param action
     * @since 2.5.0
     */
    public void forEachGlobal(BiConsumer<String, Object> action) {
        Objects.requireNonNull(action);
        this.globalVars.forEach(action);
    }

    public boolean hasGlobal(String name) {
        return this.globalVars.containsKey(name);
    }

    public Object getGlobal(String key) {
        return this.globalVars.get(key);
    }

    public void setGlobal(String key, Object value) {
        this.globalVars.put(key, value);
    }

    public boolean hasConst(String name) {
        return this.constVars.containsKey(name);
    }

    public Object getConst(String name) {
        return this.constVars.get(name);
    }

    public void setConst(String key, Object value) {
        this.constVars.put(key, value);
    }

    /**
     * @param key
     * @param method
     * @since 2.5.0
     */
    public void setConstMethod(String key, MethodDeclare method) {
        setConst(key, method);
    }

    public Bag getConstBag() {
        return new Bag() {

            @Override
            public Object get(Object key) {
                return GlobalManager.this.getConst(String.valueOf(key));
            }

            @Override
            public void set(Object key, Object value) {
                GlobalManager.this.setConst(String.valueOf(key), value);
            }
        };
    }

    public Bag getGlobalBag() {
        return new Bag() {

            @Override
            public Object get(Object key) {
                return GlobalManager.this.getGlobal(String.valueOf(key));
            }

            @Override
            public void set(Object key, Object value) {
                GlobalManager.this.setGlobal(String.valueOf(key), value);
            }
        };
    }
}
