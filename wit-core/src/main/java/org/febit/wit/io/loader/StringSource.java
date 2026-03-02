// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.io.loader;

import org.febit.wit.io.Source;

import java.io.Reader;
import java.io.StringReader;

public record StringSource(
        String text,
        BeginWith beginWith
) implements Source {

    @Override
    public Reader open() {
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
