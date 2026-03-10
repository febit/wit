package org.febit.wit.runtime.heap;

import org.febit.wit.exception.ScriptEvaluateException;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public record GenericHeap(
        Map<String, @Nullable Object> table
) implements Heap {

    public static GenericHeap local() {
        return new GenericHeap(new HashMap<>());
    }

    public static GenericHeap concurrent() {
        return new GenericHeap(new ConcurrentHashMap<>());
    }

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
