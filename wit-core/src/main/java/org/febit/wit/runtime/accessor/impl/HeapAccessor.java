// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.accessor.impl;

import org.febit.wit.runtime.Undefined;
import org.febit.wit.runtime.accessor.Getter;
import org.febit.wit.runtime.accessor.Setter;
import org.febit.wit.runtime.heap.Heap;
import org.jspecify.annotations.Nullable;

public class HeapAccessor implements Getter<Heap>, Setter<Heap> {

    @Nullable
    @Override
    public Object get(Heap heap, @Nullable Object property) {
        if (property == null) {
            return Undefined.UNDEFINED;
        }
        return heap.get(property.toString());
    }

    @Override
    public void set(Heap heap, @Nullable Object property, @Nullable Object value) {
        if (property == null) {
            // Ignore assignment to null property
            return;
        }
        heap.set(property.toString(), value);
    }
}
