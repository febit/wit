// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.extern.lib.cache.impl;

import lombok.RequiredArgsConstructor;
import org.febit.wit.extern.lib.cache.Cache;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

@RequiredArgsConstructor(staticName = "create")
public class SimpleCache<K, V> implements Cache<K, V> {

    private final Map<@Nullable K, V> cache;

    public static <K, V> SimpleCache<K, V> ofLru(int capacity) {
        var lru = new LruCache<K, V>(capacity);
        return create(Collections.synchronizedMap(lru));
    }

    @Override
    public V computeIfAbsent(@Nullable K key, Supplier<V> supplier) {
        return this.cache.computeIfAbsent(key, k -> supplier.get());
    }

    @Nullable
    @Override
    public V get(@Nullable K key) {
        return this.cache.get(key);
    }

    @Override
    public void remove(@Nullable K key) {
        this.cache.remove(key);
    }

    @Override
    public void clear() {
        this.cache.clear();
    }

    private static class LruCache<K, V>
            extends LinkedHashMap<K, V> {
        private final int capacity;

        public LruCache(int capacity) {
            super(capacity, 0.75f, true);
            this.capacity = capacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > capacity;
        }
    }
}
