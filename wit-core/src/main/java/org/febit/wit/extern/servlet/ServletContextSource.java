// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.extern.servlet;

import jakarta.servlet.ServletContext;
import org.febit.wit.exception.NoSuchSourceException;
import org.febit.wit.runtime.Source;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

public record ServletContextSource(
        String path,
        Charset charset,
        ServletContext servletContext,
        Source.BeginWith beginWith
) implements Source {

    @Override
    public Reader openReader() throws IOException {
        var in = servletContext.getResourceAsStream(path);
        if (in != null) {
            return new InputStreamReader(in, charset);
        }
        throw new NoSuchSourceException("No such resource: " + path);
    }

    @Override
    public boolean exists() {
        try {
            return servletContext.getResource(path) != null;
        } catch (Exception ignored) {
            // ignore
        }
        return false;
    }

    @Override
    public long version() {
        return 0L;
    }
}
