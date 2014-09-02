// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.lang;

/**
 *
 * @param <K> the type of keys maintained by this bag
 * @param <V> the type of mapped values
 *
 * @author zqq90
 * @since 1.5.0
 */
public interface UnsetableBag<K, V> {

    V get(K key);

    void set(K key, V value);

}
