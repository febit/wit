// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.loaders.impl;

import webit.script.exceptions.ResourceNotFoundException;
import webit.script.loaders.AbstractLoader;
import webit.script.loaders.Resource;
import webit.script.loaders.impl.resources.FileResource;

/**
 *
 * @author Zqq
 */
public class FileLoader extends AbstractLoader {

    public Resource get(String name) throws ResourceNotFoundException {
        return new FileResource(getRealPath(name), encoding);
    }
}
