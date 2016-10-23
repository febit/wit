// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.loaders.impl.resources;

import java.io.IOException;
import java.io.Reader;
import org.febit.wit.exceptions.ResourceNotFoundException;
import org.febit.wit.loaders.Resource;

/**
 * @version 1.5.0
 * @author zqq90
 */
public class NotFoundResource implements Resource {

    protected String name;
    protected String message;

    public NotFoundResource(String name, String message) {
        this.name = name;
        this.message = message;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public Reader openReader() throws IOException {
        throw new ResourceNotFoundException(message);
    }
}
