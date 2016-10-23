// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.tools.cache.impl;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.febit.wit.tools.cache.CacheProvider;

/**
 *
 * @author zqq90
 */
public class SimpleCacheProvider implements CacheProvider {

    protected int timeToLive = 100 * 60 * 1000;
    protected int multiOfMissCount = 3;

    protected final ConcurrentMap<Object, CachingEntry> cacheMap;
    protected int missCountN = 0;  // missCount x multiOfMissCount

    public SimpleCacheProvider() {
        this.cacheMap = new ConcurrentHashMap<>();
    }

    @Override
    public Object get(final Object key) {
        final CachingEntry cachingEntry;
        if ((cachingEntry = this.cacheMap.get(key)) != null) {
            if (cachingEntry.createTime + timeToLive > System.currentTimeMillis()) {
                return cachingEntry.value;
            } else {
                missCountN += multiOfMissCount;
            }
        }
        return null;
    }

    @Override
    public void put(final Object key, final Object value) {
        if (missCountN > this.cacheMap.size()) {
            missCountN = 0;
            pruneCache();
        }
        this.cacheMap.putIfAbsent(key, new CachingEntry(key, value));
    }

    //XXX: prune 1/N  each time
    protected void pruneCache() {
        final long expired = System.currentTimeMillis() - timeToLive;
        for (Iterator<CachingEntry> it = this.cacheMap.values().iterator(); it.hasNext();) {
            if (it.next().createTime < expired) {
                it.remove();
            }
        }
    }

    @Override
    public void remove(final Object key) {
        this.cacheMap.remove(key);
    }

    @Override
    public void clear() {
        this.cacheMap.clear();
    }

    static class CachingEntry {

        //final Object key;
        final Object value;
        final long createTime;

        public CachingEntry(Object key, Object value) {
            //this.key = key;
            this.value = value;
            this.createTime = System.currentTimeMillis();
        }
    }
}
