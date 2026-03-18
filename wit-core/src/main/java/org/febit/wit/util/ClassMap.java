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
package org.febit.wit.util;

import org.jspecify.annotations.Nullable;

public final class ClassMap<V> {

    private static final int MAXIMUM_CAPACITY = 1 << 29;

    private @Nullable Entry<V>[] table;
    private int threshold;
    private int size;

    @SuppressWarnings("unchecked")
    public ClassMap(int initialCapacity) {
        int initLen;
        if (initialCapacity > MAXIMUM_CAPACITY) {
            initLen = MAXIMUM_CAPACITY;
        } else {
            initLen = 16;
            while (initLen < initialCapacity) {
                initLen <<= 1;
            }
        }
        this.table = new Entry[initLen];
        this.threshold = (int) (initLen * 0.75f);
    }

    public ClassMap() {
        this(64);
    }

    public int size() {
        return size;
    }

    @Nullable
    public V unsafeGet(Class<?> key) {
        var tab = table;
        var e = tab[key.hashCode() & (tab.length - 1)];
        while (e != null) {
            if (key == e.key) {
                return e.value;
            }
            e = e.next;
        }
        return null;
    }

    @Nullable
    public V get(Class<?> key) {
        synchronized (this) {
            return unsafeGet(key);
        }
    }

    @SuppressWarnings({
            "unchecked",
            "squid:ForLoopCounterChangedCheck"
    })
    private void resize() {
        synchronized (this) {
            if (size < threshold) {
                return;
            }

            var oldTable = table;
            int oldCapacity = oldTable.length;
            int newCapacity = oldCapacity << 1;
            if (newCapacity > MAXIMUM_CAPACITY) {
                if (threshold == MAXIMUM_CAPACITY - 1) {
                    throw new IllegalStateException("Capacity exhausted.");
                }
                threshold = MAXIMUM_CAPACITY - 1;
                return;
            }

            int newMark = newCapacity - 1;
            var newTable = new Entry[newCapacity];
            for (int i = oldCapacity; i-- > 0; ) {
                int index;
                for (Entry<V> old = oldTable[i], e; old != null; ) {
                    e = old;
                    old = old.next;

                    index = e.hash & newMark;
                    e.next = newTable[index];
                    newTable[index] = e;
                }
            }

            this.threshold = (int) (newCapacity * 0.75f);
            //Note: must at Last
            this.table = newTable;
        }
    }

    @SuppressWarnings({
            "squid:ForLoopCounterChangedCheck"
    })
    public V putIfAbsent(Class<?> key, V value) {
        synchronized (this) {
            var hash = key.hashCode();
            var tab = table;
            int index = hash & (tab.length - 1);

            var e = tab[index];
            for (; e != null; e = e.next) {
                if (key == e.key) {
                    return e.value;
                }
            }

            if (size >= threshold) {
                resize();
                tab = table;
                index = hash & (tab.length - 1);
            }

            // creates the new entry.
            tab[index] = new Entry<>(hash, key, value, tab[index]);
            size++;
            return value;
        }
    }

    private static final class Entry<V> {

        final int hash;
        final Class<?> key;
        V value;
        Entry<V> next;

        Entry(int hash, Class<?> key, V value, Entry<V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}
