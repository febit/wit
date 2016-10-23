// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.tools.cache.impl;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.febit.wit.tools.cache.CacheProvider;
import org.febit.wit.Init;

/**
 *
 * @author zqq90
 */
public class EhcacheProvider implements CacheProvider {

    protected String cacheName;
    protected Ehcache ehcache;

    @Init
    public void init() {
        this.ehcache = CacheManager.getInstance().getEhcache(cacheName);
        if (this.ehcache == null) {
            throw new RuntimeException("Not found in Ehcache configuration: " + cacheName);
        }
    }

    @Override
    public Object get(Object key) {
        final Element element;
        if ((element = this.ehcache.get(key)) != null) {
            return element.getObjectValue();
        }
        return null;
    }

    @Override
    public void put(Object key, Object value) {
        this.ehcache.putIfAbsent(new Element(key, value));
    }

    @Override
    public void remove(Object key) {
        this.ehcache.remove(key);
    }

    @Override
    public void clear() {
        this.ehcache.removeAll();
    }
}
