// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util.collection;

public final class ClassIdentityHashMap<V> {

    private static final int DEFAULT_CAPACITY = 64;
    private static final int MINIMUM_CAPACITY = 4;
    private static final int MAXIMUM_CAPACITY = 1 << 29;
    private Entry<V> table[];
    private int threshold;
    private int count;
    //
    private final Object lock = new Object();

    public ClassIdentityHashMap(int initialCapacity) {

        int initlen;
        if (initialCapacity > MAXIMUM_CAPACITY || initialCapacity < 0) {
            initlen = MAXIMUM_CAPACITY;
        } else {
            initlen = MINIMUM_CAPACITY;
            while (initlen < initialCapacity) {
                initlen <<= 1;
            }
        }
        init(initlen);
    }

    public ClassIdentityHashMap() {
        init(DEFAULT_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    private void init(int initlen) {
        this.table = new Entry[initlen];
        this.threshold = (int) (initlen * 0.75f);
    }

    public int size() {
        return count;
    }

    public V unsafeGet(final Class key) {

        Entry<V> e;
        //final int id;
        //id = key.hashCode();
        final Entry<V>[] tab;
        //tab = table;
        //final int index = id % tab.length;
        //Entry<V> e = tab[(id = System.identityHashCode(key)) & (tab.length - 1)];
        //Entry<V> e = table[index];
        e = (tab = table)[key.hashCode() & (tab.length - 1)];
        while (e != null) {
            if (key == e.key) {
                return e.value;
            }
            e = e.next;
        }
        return null;
    }

    public V get(Class key) {
        synchronized (lock) {
            return unsafeGet(key);
        }
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        synchronized (lock) {
            if (count < threshold) {
                return;
            }
            final Entry<V>[] oldTable = table;
            final int oldCapacity = oldTable.length;

            final int newCapacity = oldCapacity << 1;
            if (newCapacity > MAXIMUM_CAPACITY) {
                if (threshold == MAXIMUM_CAPACITY - 1) {
                    throw new IllegalStateException("Capacity exhausted.");
                }
                threshold = MAXIMUM_CAPACITY - 1;  // Gigantic map!
                return;
            }
            final int newMark = newCapacity - 1;
            final Entry<V> newTable[] = new Entry[newCapacity];

            for (int i = oldCapacity; i-- > 0;) {
                int index;
                for (Entry<V> old = oldTable[i], e; old != null;) {
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
    private V unsafePutIfAbsent(Class key, V value) {

        final int id;
        //id = key.hashCode();
        int index;
        //final int index = id % tab.length;

        Entry<V>[] tab;
        //tab = table;
        Entry<V> e = (tab = table)[index = (id = key.hashCode()) & (tab.length - 1)];
        //Entry<V> e = table[index];
        for (; e != null; e = e.next) {
            if (e.id == id) {
                //V old = e.value;
                //e.value = value;
                return e.value;
            }
        }

        if (count >= threshold) {
            resize();
            tab = table;
            index = id & (tab.length - 1);
        }

        // creates the new entry.
        tab[index] = new Entry(id, key, value, tab[index]);
        count++;
        return value;
    }

    public V putIfAbsent(Class key, V value) {
        synchronized (lock) {
            return unsafePutIfAbsent(key, value);
        }
    }

    private static final class Entry<V> {

        final int id;
        final Class key;
        final V value;
        Entry<V> next;

        Entry(int id, Class key, V value, Entry<V> next) {
            this.value = value;
            this.id = id;
            this.key = key;
            this.next = next;
        }
    }
}
