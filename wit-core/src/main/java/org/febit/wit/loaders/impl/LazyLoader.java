// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.loaders.impl;

import org.febit.wit.loaders.Loader;
import org.febit.wit.loaders.Resource;
import org.febit.wit.loaders.impl.resources.LazyResource;

/**
 *
 * @since 1.4.0
 * @author zqq90
 */
public class LazyLoader implements Loader {

    protected int timeout;
    protected Loader loader;

    @Override
    public Resource get(String name) {
        if (this.timeout > 0) {
            return new LazyResource(this.loader.get(name), this.timeout);
        } else {
            return this.loader.get(name);
        }
    }

    @Override
    public String concat(String parent, String name) {
        return this.loader.concat(parent, name);
    }

    @Override
    public String normalize(String name) {
        return this.loader.normalize(name);
    }

    @Override
    public boolean isEnableCache(String name) {
        return this.loader.isEnableCache(name);
    }
}
