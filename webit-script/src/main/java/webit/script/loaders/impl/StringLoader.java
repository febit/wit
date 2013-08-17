// Copyright (c) 2013, Webit Team. All Rights Reserved.

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

    public Resource get(String name) throws ResourceNotFoundException {
        return new StringResource(name);
    }

    public String concat(String parent, String name) {
        //ignore parent
        return name;
    }

    public String normalize(String name) {
        return name;
    }
}
