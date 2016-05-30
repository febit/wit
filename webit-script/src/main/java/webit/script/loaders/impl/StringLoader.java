// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.loaders.impl;

import webit.script.loaders.Loader;
import webit.script.loaders.Resource;
import webit.script.loaders.impl.resources.StringResource;

/**
 *
 * @author zqq90
 */
public class StringLoader implements Loader {

    protected boolean enableCache;

    @Override
    public Resource get(String name) {
        return new StringResource(name);
    }

    @Override
    public String concat(String parent, String name) {
        //ignore parent
        return name;
    }

    @Override
    public String normalize(String name) {
        return name;
    }

    @Override
    public boolean isEnableCache(String name) {
        return enableCache;
    }
}
