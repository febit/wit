// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.loaders.impl;

import org.febit.wit.loaders.Loader;
import org.febit.wit.loaders.Resource;
import org.febit.wit.loaders.impl.resources.NotFoundResource;
import org.febit.wit.util.ArrayUtil;

/**
 *
 * @author zqq90
 */
public class SecurityLoader implements Loader {

    protected String[] list = ArrayUtil.EMPTY_STRINGS;
    protected Loader loader;

    @Override
    public Resource get(String name) {
        for (String item : this.list) {
            if (name.startsWith(item)) {
                return this.loader.get(name);
            }
        }
        return new NotFoundResource(name, "Security Unaccessable: " + name);
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
