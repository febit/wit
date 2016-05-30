// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.loaders.impl;

import webit.script.loaders.AbstractLoader;
import webit.script.loaders.Resource;
import webit.script.loaders.impl.resources.ClasspathResource;

/**
 *
 * @author zqq90
 */
public class ClasspathLoader extends AbstractLoader {

    @Override
    public Resource get(String name) {
        return new ClasspathResource(getRealPath(name), encoding);
    }
}
