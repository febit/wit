package org.febit.wit.runtime.heap;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@RequiredArgsConstructor(staticName = "create")
public class LocalHeap {

    private final Map<Object, @Nullable Object> table = new HashMap<>(16);

    public void set(Object key, @Nullable Object value) {
        table.put(key, value);
    }

    @Nullable
    public Object get(Object key) {
        return table.get(key);
    }

    public void each(BiConsumer<Object, @Nullable Object> action) {
        this.table.forEach(action);
    }
}
