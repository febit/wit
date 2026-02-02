// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.loader.impl;

import org.febit.wit.runtime.Source;

import java.io.Reader;
import java.io.StringReader;

public record StringSource(
        String text,
        BeginWith beginWith
) implements Source {

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
