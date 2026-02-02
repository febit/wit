// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.accessor.impl;

import org.febit.wit.accessor.Getter;
import org.febit.wit.accessor.Setter;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public class MapAccessor<T extends Map<?, ?>> implements Getter<T>, Setter<T> {

    @Override
    @Nullable
    public Object get(T map, @Nullable Object property) {
        return map.get(property);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void set(T map, @Nullable Object property, @Nullable Object value) {
        ((Map<Object, Object>) map).put(property, value);
    }
}
