package org.febit.wit.runtime;

import org.febit.wit.exceptions.ScriptRuntimeException;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;

public interface Heap {

    void set(String name, @Nullable Object value);

    @Nullable
    Object get(String name, boolean strict) throws ScriptRuntimeException;

    @Nullable
    default Object get(String name) throws ScriptRuntimeException {
        return get(name, true);
    }

    void each(BiConsumer<String, @Nullable Object> action);

    default void exportTo(Map<? super String, @Nullable Object> map) {
        each(map::put);
    }
}
