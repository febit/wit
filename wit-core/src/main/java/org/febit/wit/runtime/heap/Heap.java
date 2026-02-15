package org.febit.wit.runtime.heap;

import org.febit.wit.exception.ScriptEvaluateException;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;

public interface Heap {

    void set(String name, @Nullable Object value);

    @Nullable
    Object get(String name, boolean strict) throws ScriptEvaluateException;

    @Nullable
    default Object get(String name) throws ScriptEvaluateException {
        return get(name, true);
    }

    void each(BiConsumer<String, @Nullable Object> action);

    default void exportTo(Map<? super String, @Nullable Object> map) {
        each(map::put);
    }
}
