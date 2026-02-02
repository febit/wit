// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.iter;

import lombok.RequiredArgsConstructor;
import org.febit.wit.lang.KeyIter;
import org.jspecify.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public final class MapKeyIter<K, V> extends AbstractIter implements KeyIter {

    private final Iterator<Map.Entry<K, V>> iterator;

    private Map.@Nullable Entry<K, V> current;

    public MapKeyIter(Map<K, V> map) {
        this.iterator = map.entrySet().iterator();
    }

    @Override
    public Object value() {
        Objects.requireNonNull(this.current);
        return this.current.getValue();
    }

    @Override
    protected Object _next() {
        return (this.current = iterator.next()).getKey();
    }

    @Override
    public boolean hasNext() {
        return this.iterator.hasNext();
    }
}
