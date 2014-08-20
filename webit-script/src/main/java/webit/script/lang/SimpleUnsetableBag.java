// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.lang;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @param <K> the type of keys maintained by this bag
 * @param <V> the type of mapped values
 *
 * @author zqq90
 * @since 1.4.0
 */
public class SimpleUnsetableBag<K, V> implements UnsetableBag<K, V> {

    private final Map<K, V> values;

    public SimpleUnsetableBag() {
        this.values = new HashMap<K, V>();
    }

    public V get(K key) {
        return this.values.get(key);
    }

    public void set(K key, V value) {
        this.values.put(key, value);
    }
}
