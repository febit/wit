// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.loaders.impl.resources;

import org.febit.wit.exceptions.ResourceNotFoundException;
import org.febit.wit.lang.Resource;

import java.io.IOException;
import java.io.Reader;

/**
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
    public boolean exists() {
        return false;
    }

    @Override
    public Reader openReader() throws IOException {
        throw new ResourceNotFoundException(message);
    }

    /**
     * @since 2.0.0
     */
    @Override
    public boolean isCodeFirst() {
        return false;
    }

    @Override
    public long version() {
        return 0L;
    }
}
