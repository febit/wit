// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.loaders.impl;

import webit.script.Engine;
import webit.script.Initable;
import webit.script.loaders.Loader;
import webit.script.loaders.Resource;
import webit.script.loaders.impl.resources.NotFoundResource;
import webit.script.util.ClassEntry;
import webit.script.util.StringUtil;

/**
 *
 * @author zqq90
 */
public class SimpleSecurityLoader implements Loader, Initable {

    protected String[] list = StringUtil.EMPTY_ARRAY;
    protected ClassEntry _loader;

    protected Loader loader;

    public void init(Engine engine) {
        this.loader = (Loader) engine.getComponent(_loader);
    }

    public Resource get(String name) {
        for (String item : this.list) {
            if (name.startsWith(item)) {
                return this.loader.get(name);
            }
        }
        return new NotFoundResource(name, "Security Unaccessable: " + name);
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

    public void setList(String list) {
        this.list = StringUtil.splitAndRemoveBlank(list);
    }

    public void setLoader(ClassEntry _loader) {
        this._loader = _loader;
    }
}
