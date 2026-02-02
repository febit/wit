// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.extern.lib.cache.impl;

import org.febit.wit.extern.lib.cache.Cache;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public class NoopCache<K, V> implements Cache<K, V> {

    @Override
    public V computeIfAbsent(@Nullable K key, Supplier<V> supplier) {
        return supplier.get();
    }

    @Override
    public @Nullable V get(@Nullable K key) {
        return null;
    }

    @Override
    public void remove(@Nullable K key) {
        // No-op
    }

    @Override
    public void clear() {
        // No-op
    }
}
