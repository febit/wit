// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.extern.servlet;

import jakarta.servlet.ServletContext;
import org.febit.wit.exceptions.ResourceNotFoundException;
import org.febit.wit.runtime.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

public record ServletContextResource(
        String path,
        Charset charset,
        ServletContext servletContext,
        boolean codeFirst
) implements Resource {

    @Override
    public Reader openReader() throws IOException {
        var in = servletContext.getResourceAsStream(path);
        if (in != null) {
            return new InputStreamReader(in, charset);
        }
        throw new ResourceNotFoundException("Resource Not Found: " + path);
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
