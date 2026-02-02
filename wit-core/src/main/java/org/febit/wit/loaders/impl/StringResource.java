// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.loaders.impl;

import org.febit.wit.lang.Resource;

import java.io.Reader;
import java.io.StringReader;

public record StringResource(
        String text,
        boolean codeFirst
) implements Resource {

    @Override
    public Reader openReader() {
        return new StringReader(this.text);
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public long version() {
        return 0L;
    }
}
