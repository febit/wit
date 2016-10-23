// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.loaders.impl;

import org.febit.wit.loaders.AbstractLoader;
import org.febit.wit.loaders.Resource;
import org.febit.wit.loaders.impl.resources.ClasspathResource;

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
