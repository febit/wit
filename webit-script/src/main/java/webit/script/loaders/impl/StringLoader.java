// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.loaders.impl;

import webit.script.exceptions.ResourceNotFoundException;
import webit.script.loaders.Loader;
import webit.script.loaders.Resource;
import webit.script.loaders.impl.resources.StringResource;

/**
 *
 * @author Zqq
 */
public class StringLoader implements Loader {

    protected boolean enableCache = false;

    public Resource get(String name) {
        return new StringResource(name);
    }

    public String concat(String parent, String name) {
        //ignore parent
        return name;
    }

    public String normalize(String name) {
        return name;
    }

    public boolean isEnableCache(String name) {
        return enableCache;
    }

    public void setEnableCache(boolean enableCache) {
        this.enableCache = enableCache;
    }
}
