// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.loaders.impl;

import org.febit.wit.loaders.Loader;
import org.febit.wit.loaders.Resource;
import org.febit.wit.loaders.impl.resources.LazyResource;

/**
 * @author zqq90
 * @since 1.4.0
 */
@SuppressWarnings({
        "WeakerAccess"
})
public class LazyLoader implements Loader {

    protected int timeout;
    protected Loader loader;

    @Override
    public Resource get(String name) {
        var inside = this.loader.get(name);
        return this.timeout > 0
                ? new LazyResource(inside, this.timeout)
                : inside;
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
