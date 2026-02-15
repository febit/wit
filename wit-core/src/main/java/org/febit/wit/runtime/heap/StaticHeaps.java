// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.heap;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.function.FunctionDeclare;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;

@Accessors(fluent = true)
public class StaticHeaps {

    @Getter
    private final StaticHeap constant = new StaticHeapImpl();
    @Getter
    private final StaticHeap variant = new StaticHeapImpl();

    public void clear() {
        this.constant().clear();
        this.variant().clear();
    }

    public interface StaticHeap extends Heap {

        boolean has(String name);

        void clear();

        default void setFunction(String key, FunctionDeclare method) {
            set(key, method);
        }
    }

    private static class StaticHeapImpl implements StaticHeap {

        private final ConcurrentMap<String, @Nullable Object> table = new ConcurrentHashMap<>();

        @Override
        public boolean has(String name) {
            return this.table.containsKey(name);
        }

        @Override
        public void clear() {
            this.table.clear();
        }

        @Override
        public void set(String name, @Nullable Object value) {
            this.table.put(name, value);
        }

        @Override
        public @Nullable Object get(String name, boolean strict) throws ScriptEvaluateException {
            return this.table.get(name);
        }

        @Override
        public void each(BiConsumer<String, @Nullable Object> action) {
            this.table.forEach(action);
        }
    }

}
