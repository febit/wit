// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.loaders.impl;

import org.febit.wit.exceptions.SourceNotFoundException;
import org.febit.wit.runtime.Source;

import java.io.IOException;
import java.io.Reader;

public record NotExistSource(
        String path,
        String message
) implements Source {

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public Reader openReader() throws IOException {
        throw new SourceNotFoundException(message);
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
