// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.loaders.impl;

import java.util.ArrayList;
import webit.script.Engine;
import webit.script.Initable;
import webit.script.exceptions.ResourceNotFoundException;
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

    protected String[] list = new String[0];
    protected ClassEntry _loader;

    protected Loader loader;

    public void init(Engine engine) {
        this.loader = (Loader) engine.getComponent(_loader);
    }

    public Resource get(String name) {
        final String[] whiteList = this.list;
        for (int i = 0, len = whiteList.length; i < len; i++) {
            if (name.startsWith(whiteList[i])) {
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
        String[] temp = StringUtil.splitc(list);
        ArrayList<String> result = new ArrayList<String>(temp.length);
        for (String string : temp) {
            if ((string = string.trim()).length() != 0) {
                result.add(string);
            }
        }
        result.toArray(this.list = new String[result.size()]);
    }

    public void setLoader(ClassEntry _loader) {
        this._loader = _loader;
    }
}
