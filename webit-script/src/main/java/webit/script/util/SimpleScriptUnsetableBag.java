// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util;

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
public class SimpleScriptUnsetableBag<K, V> implements ScriptUnsetableBag<K, V> {

    private final Map<K, V> values;

    public SimpleScriptUnsetableBag() {
        this.values = new HashMap<K, V>();
    }

    public V get(K key) {
        return this.values.get(key);
    }

    public void set(K key, V value) {
        this.values.put(key, value);
    }
}
