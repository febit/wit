// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.tools.cache.impl;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import webit.script.Engine;
import webit.script.Initable;
import webit.script.tools.cache.CacheProvider;

/**
 *
 * @author zqq90
 */
public class EhcacheProvider implements CacheProvider, Initable {

    protected String cacheName;
    protected Ehcache ehcache;

    public void init(Engine engine) {
        this.ehcache = CacheManager.getInstance().getEhcache(cacheName);
        if (this.ehcache == null) {
            throw new RuntimeException("Not found in Ehcache configuration: " + cacheName);
        }
    }

    public Object get(Object key) {
        final Element element;
        if ((element = this.ehcache.get(key)) != null) {
            return element.getObjectValue();
        }
        return null;
    }

    public void put(Object key, Object value) {
        this.ehcache.putIfAbsent(new Element(key, value));
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public void remove(Object key) {
        this.ehcache.remove(key);
    }

    public void clear() {
        this.ehcache.removeAll();
    }
}
