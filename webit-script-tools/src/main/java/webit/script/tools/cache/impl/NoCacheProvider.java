// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.tools.cache.impl;

import webit.script.tools.cache.CacheProvider;

/**
 *
 * @author zqq90
 */
public class NoCacheProvider implements CacheProvider {

    public void put(Object key, Object value) {
    }

    public Object get(Object key) {
        return null;
    }

    public void remove(Object key) {
    }

    public void clear() {
    }
}
