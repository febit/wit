// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.global;

import org.febit.wit.lang.Bag;
import org.febit.wit.lang.FunctionDeclare;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;

public class GlobalHeap {

    private final ConcurrentMap<String, @Nullable Object> constVars = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, @Nullable Object> globalVars = new ConcurrentHashMap<>();

    public void clear() {
        this.constVars.clear();
        this.globalVars.clear();
    }

    /**
     * Performs the given action for each const vars until all have been processed or the action throws an exception.
     */
    public void forEachConst(BiConsumer<String, @Nullable Object> action) {
        Objects.requireNonNull(action);
        this.constVars.forEach(action);
    }

    /**
     * Performs the given action for each global vars until all have been processed or the action throws an exception.
     */
    public void forEachGlobal(BiConsumer<String, @Nullable Object> action) {
        Objects.requireNonNull(action);
        this.globalVars.forEach(action);
    }

    public boolean hasGlobal(String name) {
        return this.globalVars.containsKey(name);
    }

    @Nullable
    public Object getGlobal(String key) {
        return this.globalVars.get(key);
    }

    public void setGlobal(String key, @Nullable Object value) {
        this.globalVars.put(key, value);
    }

    public boolean hasConst(String name) {
        return this.constVars.containsKey(name);
    }

    @Nullable
    public Object getConst(String name) {
        return this.constVars.get(name);
    }

    public void setConst(String key, @Nullable Object value) {
        this.constVars.put(key, value);
    }

    public void setConstMethod(String key, FunctionDeclare method) {
        setConst(key, method);
    }

    public Bag getConstBag() {
        return new Bag() {

            @Nullable
            @Override
            public Object get(@Nullable Object key) {
                return GlobalHeap.this.getConst(String.valueOf(key));
            }

            @Override
            public void set(@Nullable Object key, @Nullable Object value) {
                GlobalHeap.this.setConst(String.valueOf(key), value);
            }
        };
    }

    public Bag getGlobalBag() {
        return new Bag() {
            @Nullable
            @Override
            public Object get(@Nullable Object key) {
                return GlobalHeap.this.getGlobal(String.valueOf(key));
            }

            @Override
            public void set(@Nullable Object key, @Nullable Object value) {
                GlobalHeap.this.setGlobal(String.valueOf(key), value);
            }
        };
    }
}
