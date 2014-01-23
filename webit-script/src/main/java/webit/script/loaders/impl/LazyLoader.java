// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.loaders.impl;

import webit.script.Engine;
import webit.script.Initable;
import webit.script.exceptions.ResourceNotFoundException;
import webit.script.loaders.Loader;
import webit.script.loaders.Resource;
import webit.script.loaders.impl.resources.LazyResource;
import webit.script.util.ClassEntry;

/**
 *
 * @since 1.4.0
 * @author zqq90
 */
public class LazyLoader implements Loader, Initable {

    protected int timeout;
    protected ClassEntry _loader;

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setLoader(ClassEntry _loader) {
        this._loader = _loader;
    }
    //
    protected Loader loader;

    public void init(Engine engine) {
        try {
            this.loader = (Loader) engine.getComponent(_loader);
        } catch (InstantiationException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Resource get(String name) throws ResourceNotFoundException {
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
}
