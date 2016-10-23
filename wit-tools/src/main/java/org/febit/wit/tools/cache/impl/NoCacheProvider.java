// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.tools.cache.impl;

import org.febit.wit.tools.cache.CacheProvider;

/**
 *
 * @author zqq90
 */
public class NoCacheProvider implements CacheProvider {

    @Override
    public void put(Object key, Object value) {
    }

    @Override
    public Object get(Object key) {
        return null;
    }

    @Override
    public void remove(Object key) {
    }

    @Override
    public void clear() {
    }
}
