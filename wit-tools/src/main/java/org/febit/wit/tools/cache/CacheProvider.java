// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.tools.cache;

/**
 *
 * @author zqq90
 */
public interface CacheProvider {

    void put(Object key, Object value);

    Object get(Object key);

    void remove(Object key);

    void clear();
}
