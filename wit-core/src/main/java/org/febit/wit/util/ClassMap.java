// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

public final class ClassMap<V> {

    private static final int MAXIMUM_CAPACITY = 1 << 29;

    private Entry<V>[] table;
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

    public V unsafeGet(final Class<?> key) {
        Entry<V> e;
        final Entry<V>[] tab = table;
        e = tab[key.hashCode() & (tab.length - 1)];
        while (e != null) {
            if (key == e.key) {
                return e.value;
            }
            e = e.next;
        }
        return null;
    }

    public V get(Class<?> key) {
        synchronized (this) {
            return unsafeGet(key);
        }
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        synchronized (this) {
            if (size < threshold) {
                return;
            }
            final Entry<V>[] oldTable = table;
            final int oldCapacity = oldTable.length;

            final int newCapacity = oldCapacity << 1;
            if (newCapacity > MAXIMUM_CAPACITY) {
                if (threshold == MAXIMUM_CAPACITY - 1) {
                    throw new IllegalStateException("Capacity exhausted.");
                }
                threshold = MAXIMUM_CAPACITY - 1;
                return;
            }
            final int newMark = newCapacity - 1;
            final Entry<V>[] newTable = new Entry[newCapacity];

            for (int i = oldCapacity; i-- > 0; ) {
                int index;
                for (Entry<V> old = oldTable[i], e; old != null; ) {
                    e = old;
                    old = old.next;

                    index = e.id & newMark;
                    e.next = newTable[index];
                    newTable[index] = e;
                }
            }

            this.threshold = (int) (newCapacity * 0.75f);
            //Note: must at Last
            this.table = newTable;
        }
    }

    @SuppressWarnings("unchecked")
    public V putIfAbsent(Class<?> key, V value) {
        synchronized (this) {
            final int id = key.hashCode();
            Entry<V>[] tab = table;
            int index = id & (tab.length - 1);

            Entry<V> e = tab[index];
            for (; e != null; e = e.next) {
                if (key == e.key) {
                    return e.value;
                }
            }

            if (size >= threshold) {
                resize();
                tab = table;
                index = id & (tab.length - 1);
            }

            // creates the new entry.
            tab[index] = new Entry(id, key, value, tab[index]);
            size++;
            return value;
        }
    }

    private static final class Entry<V> {

        final int id;
        final Class<?> key;
        V value;
        Entry<V> next;

        Entry(int id, Class<?> key, V value, Entry<V> next) {
            this.value = value;
            this.id = id;
            this.key = key;
            this.next = next;
        }
    }
}
