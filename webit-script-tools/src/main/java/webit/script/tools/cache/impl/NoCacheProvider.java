// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.tools.cache.impl;

import webit.script.tools.cache.CacheProvider;

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
