// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.loader.impl;

import org.febit.wit.exception.NoSuchSourceException;
import org.febit.wit.runtime.Source;

import java.io.IOException;
import java.io.Reader;

public record NoSuchSource(
        String path,
        String message
) implements Source {

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public Reader openReader() throws IOException {
        throw new NoSuchSourceException(message);
    }

    @Override
    public BeginWith beginWith() {
        return BeginWith.SCRIPT;
    }

    @Override
    public long version() {
        return 0L;
    }
}
