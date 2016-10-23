// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.lang;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @param <K> the type of keys maintained by this bag
 * @param <V> the type of mapped values
 *
 * @author zqq90
 * @since 1.5.0
 */
public class SimpleUnsetableBag<K, V> implements UnsetableBag<K, V> {

    private final Map<K, V> values;

    public SimpleUnsetableBag() {
        this.values = new HashMap<>();
    }

    @Override
    public V get(K key) {
        return this.values.get(key);
    }

    @Override
    public void set(K key, V value) {
        this.values.put(key, value);
    }
}
