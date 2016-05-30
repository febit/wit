// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.tools.cache;

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
