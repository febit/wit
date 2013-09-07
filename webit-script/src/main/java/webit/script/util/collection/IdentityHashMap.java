// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util.collection;

public final class IdentityHashMap<V> {

    private Entry<V> table[];
    private int threshold;
    private int count;
    private final float loadFactor;
    //
    private final Object lock = new Object();

    @SuppressWarnings("unchecked")
    public IdentityHashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Invalid initial capacity: " + initialCapacity);
        }
        if (loadFactor <= 0) {
            throw new IllegalArgumentException("Invalid load factor: " + loadFactor);
        }
        if (initialCapacity == 0) {
            initialCapacity = 1;
        }
        this.loadFactor = loadFactor;
        table = new Entry[initialCapacity];
        threshold = (int) (initialCapacity * loadFactor);
    }

    public IdentityHashMap(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }

    public IdentityHashMap() {
        this(101, 0.75f);
    }

    public int size() {
        return count;
    }

    public V unsafeGet(Object key) {
        final Entry<V> tab[] = table;

        final int id = System.identityHashCode(key);
        final int index = id % tab.length;

        for (Entry<V> e = tab[index]; e != null; e = e.next) {
            if (e.id == id) {
                return e.value;
            }
        }

        return null;
    }

    public V get(Object key) {
        synchronized(lock){
            return unsafeGet(key);
        }
    }
    
    @SuppressWarnings("unchecked")
    private void resize() {
        synchronized(lock){
            if (count < threshold) {
                return;
            }
            final int oldCapacity = table.length;
            final Entry<V> oldTable[] = table;

            final int newCapacity = (oldCapacity << 1) + 1;
            final Entry<V> newTable[] = new Entry[newCapacity];


            for (int i = oldCapacity; i-- > 0;) {
                for (Entry<V> old = oldTable[i]; old != null;) {
                    Entry<V> e = old;
                    old = old.next;

                    int index = e.id % newCapacity;
                    e.next = newTable[index];
                    newTable[index] = e;
                }
            }

            threshold = (int) (newCapacity * loadFactor);
            //Note: must at Last
            table = newTable;
        }
    }

    @SuppressWarnings("unchecked")
    public V unsafePutIfAbsent(Object key, V value) {

        Entry<V> tab[] = table;

        final int id = System.identityHashCode(key);

        int index = id % tab.length;
        for (Entry<V> e = tab[index]; e != null; e = e.next) {
            if (e.id == id) {
                V old = e.value;
                //e.value = value;
                return old;
            }
        }

        if (count >= threshold) {
            resize();

            tab = table;
            index = id % tab.length;
        }

        // creates the new entry.
        tab[index] = new Entry(id, value, tab[index]);
        count++;
        return value;
    }
    
    public V putIfAbsent(Object key, V value) {
        synchronized(lock){
            return unsafePutIfAbsent(key, value);
        }
    }

    private static final class Entry<V> {

        final int id;
        V value;
        Entry<V> next;

        Entry(int id, V value, Entry<V> next) {
            this.value = value;
            this.id = id;
            this.next = next;
        }
    }
}
