// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.loaders.impl;

import webit.script.exceptions.ResourceNotFoundException;
import webit.script.loaders.AbstractLoader;
import webit.script.loaders.Resource;
import webit.script.loaders.impl.resources.ClasspathResource;

/**
 *
 * @author Zqq
 */
public class ClasspathLoader extends AbstractLoader {

    public Resource get(String name) throws ResourceNotFoundException {
        return new ClasspathResource(getRealPath(name), encoding);
    }
}
