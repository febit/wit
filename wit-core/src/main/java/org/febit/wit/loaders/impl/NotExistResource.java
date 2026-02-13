// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.loaders.impl;

import org.febit.wit.exceptions.ResourceNotFoundException;
import org.febit.wit.runtime.Resource;

import java.io.IOException;
import java.io.Reader;

public record NotExistResource(
        String path,
        String message
) implements Resource {

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public Reader openReader() throws IOException {
        throw new ResourceNotFoundException(message);
    }

    @Override
    public boolean codeFirst() {
        return false;
    }

    @Override
    public long version() {
        return 0L;
    }
}
