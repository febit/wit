// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.iter;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;

@RequiredArgsConstructor(staticName = "of")
public final class MapKeyIter<K, V> implements KeyIter {

    private final Cursor cursor = new Cursor();
    private final Iterator<Map.Entry<K, V>> iterator;

    private Map.@Nullable Entry<K, V> current;

    public static <K, V> MapKeyIter<K, V> of(Map<K, V> map) {
        return of(map.entrySet().iterator());
    }

    @Override
    public Object value() {
        if (this.current == null) {
            throw new IllegalStateException("No current entry");
        }
        return this.current.getValue();
    }

    @Override
    public Object next() {
        Map.@Nullable Entry<K, V> e;
        this.current = e = iterator.next();
        cursor.next();
        return e.getKey();
    }

    @Override
    public int index() {
        return cursor.get();
    }

    @Override
    public boolean hasNext() {
        return this.iterator.hasNext();
    }
}
