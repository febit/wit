// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.extern.lib.cache;

import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public interface Cache<K, V> {

    V computeIfAbsent(@Nullable K key, Supplier<V> supplier);

    @Nullable
    V get(@Nullable K key);

    void remove(@Nullable K key);

    void clear();
}
