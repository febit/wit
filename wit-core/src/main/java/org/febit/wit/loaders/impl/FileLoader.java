// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.loaders.impl;

import org.febit.wit.loaders.AbstractLoader;
import org.febit.wit.loaders.Resource;
import org.febit.wit.loaders.impl.resources.FileResource;

/**
 * @author zqq90
 */
public class FileLoader extends AbstractLoader {

    @Override
    public Resource get(String name) {
        return new FileResource(getRealPath(name), encoding, codeFirst);
    }
}
