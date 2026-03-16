package org.febit.wit.runtime.heap;

import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.WitFunction;
import org.jspecify.annotations.Nullable;

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

    default void setAsFunction(String key, WitFunction func) {
        set(key, func);
    }

    default void setAsFunction(String key, WitFunction.Constable func) {
        set(key, func);
    }
}
