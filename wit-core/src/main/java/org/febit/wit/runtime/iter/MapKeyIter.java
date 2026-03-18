/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
