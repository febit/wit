// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.loader.impl;

import org.febit.wit.exception.NoSuchSourceException;
import org.febit.wit.runtime.Source;
import org.febit.wit.util.ClassUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

public record ClasspathSource(
        String path,
        Charset charset,
        BeginWith beginWith
) implements Source {

    @Override
    public boolean exists() {
        return ClassUtils.getDefaultClassLoader().getResource(path) != null;
    }

    @Override
    public Reader openReader() throws IOException {
        var in = ClassUtils.getDefaultClassLoader()
                .getResourceAsStream(path);
        if (in == null) {
            throw new NoSuchSourceException("No such resource: " + path);
        }
        return new InputStreamReader(in, charset);
    }

    @Override
    public long version() {
        return 0L;
    }
}
