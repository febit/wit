package org.febit.wit.runtime.heap;

import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.Function;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;

public interface Heap {

    boolean has(String name);

    void set(String name, @Nullable Object value);

    @Nullable
    Object get(String name, boolean strict) throws ScriptEvaluateException;

    void clear();

    @Nullable
    default Object get(String name) throws ScriptEvaluateException {
        return get(name, true);
    }

    void each(BiConsumer<String, @Nullable Object> action);

    default void setFunction(String key, Function method) {
        set(key, method);
    }

    default void setFunction(String key, Function.Constable method) {
        set(key, method);
    }

    default void exportTo(Map<? super String, @Nullable Object> map) {
        each(map::put);
    }
}
