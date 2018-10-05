// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.servlet.loaders;

import org.febit.wit.exceptions.ResourceNotFoundException;
import org.febit.wit.loaders.Resource;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @author zqq90
 */
public class ServletContextResource implements Resource {

    protected final String path;
    protected final String encoding;
    protected final ServletContext servletContext;
    protected final boolean codeFirst;

    /**
     * @param path
     * @param encoding
     * @param servletContext
     */
    public ServletContextResource(String path, String encoding, ServletContext servletContext) {
        this(path, encoding, servletContext, false);
    }

    /**
     * @param path
     * @param encoding
     * @param servletContext
     * @param codeFirst
     * @since 2.0.0
     */
    public ServletContextResource(String path, String encoding, ServletContext servletContext, boolean codeFirst) {
        this.path = path;
        this.encoding = encoding;
        this.servletContext = servletContext;
        this.codeFirst = codeFirst;
    }

    @Override
    public Reader openReader() throws IOException {
        final InputStream in = servletContext.getResourceAsStream(path);
        if (in != null) {
            return new InputStreamReader(in, encoding);
        }
        throw new ResourceNotFoundException("Resource Not Found: ".concat(path));
    }

    /**
     * @since 1.4.1
     */
    @Override
    public boolean exists() {
        try {
            if (servletContext.getResource(path) != null) {
                return true;
            }
        } catch (Exception ignore) {
        }
        return false;
    }

    /**
     * @since 2.0.0
     */
    @Override
    public boolean isCodeFirst() {
        return codeFirst;
    }

    @Override
    public long version() {
        return 0L;
    }
}
