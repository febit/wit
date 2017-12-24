// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.loaders.impl.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.febit.wit.exceptions.ResourceNotFoundException;
import org.febit.wit.loaders.Resource;
import org.febit.wit.util.ClassUtil;
import org.febit.wit.util.InternedEncoding;

/**
 *
 * @author zqq90
 */
public class ClasspathResource implements Resource {

    protected final String path;
    protected final String encoding;
    protected final boolean codeFirst;

    /**
     *
     * @param path
     * @param encoding
     */
    public ClasspathResource(String path, String encoding) {
        this(path, encoding, false);
    }

    /**
     *
     * @param path
     * @param encoding
     * @param codeFirst
     * @since 2.0.0
     */
    public ClasspathResource(String path, String encoding, boolean codeFirst) {
        this.path = path;
        this.encoding = encoding != null ? encoding : InternedEncoding.UTF_8.value;
        this.codeFirst = codeFirst;
    }

    /**
     * @since 1.4.1
     */
    @Override
    public boolean exists() {
        return ClassUtil.getDefaultClassLoader().getResource(path) != null;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public Reader openReader() throws IOException {
        final InputStream in = ClassUtil.getDefaultClassLoader()
                .getResourceAsStream(path);
        if (in != null) {
            return new InputStreamReader(in, encoding);
        } else {
            throw new ResourceNotFoundException("Resource Not Found: ".concat(path));
        }
    }

    /**
     * @since 2.0.0
     */
    @Override
    public boolean isCodeFirst() {
        return codeFirst;
    }
}
