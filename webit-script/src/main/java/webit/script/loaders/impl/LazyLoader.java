// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.loaders.impl;

import webit.script.loaders.Loader;
import webit.script.loaders.Resource;
import webit.script.loaders.impl.resources.LazyResource;

/**
 *
 * @since 1.4.0
 * @author zqq90
 */
public class LazyLoader implements Loader {

    protected int timeout;
    protected Loader loader;

    public Resource get(String name) {
        if (this.timeout > 0) {
            return new LazyResource(this.loader.get(name), this.timeout);
        } else {
            return this.loader.get(name);
        }
    }

    public String concat(String parent, String name) {
        return this.loader.concat(parent, name);
    }

    public String normalize(String name) {
        return this.loader.normalize(name);
    }

    public boolean isEnableCache(String name) {
        return this.loader.isEnableCache(name);
    }
}
