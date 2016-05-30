// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.loaders.impl;

import webit.script.loaders.Loader;
import webit.script.loaders.Resource;
import webit.script.loaders.impl.resources.NotFoundResource;
import webit.script.util.ArrayUtil;

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
