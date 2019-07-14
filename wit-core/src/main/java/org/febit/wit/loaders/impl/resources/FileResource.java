// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.loaders.impl.resources;

import org.febit.wit.loaders.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @author zqq90
 */
@SuppressWarnings({
        "WeakerAccess"
})
public class FileResource implements Resource {

    protected final File file;
    protected final String encoding;
    protected final boolean codeFirst;

    public FileResource(String path, String encoding) {
        this(path, encoding, false);
    }

    /**
     * @since 2.0.0
     */
    public FileResource(File file, String encoding) {
        this(file, encoding, false);
    }

    /**
     * @since 2.0.0
     */
    public FileResource(String path, String encoding, boolean codeFirst) {
        this(new File(path), encoding, codeFirst);
    }

    /**
     * @since 2.0.0
     */
    public FileResource(File file, String encoding, boolean codeFirst) {
        this.encoding = encoding;
        this.file = file;
        this.codeFirst = codeFirst;
    }

    @Override
    public long version() {
        return file.lastModified();
    }

    @Override
    public Reader openReader() throws IOException {
        return new InputStreamReader(
                new FileInputStream(file),
                encoding);
    }

    /**
     * @since 1.4.1
     */
    @Override
    public boolean exists() {
        return file.exists();
    }

    /**
     * @since 2.0.0
     */
    @Override
    public boolean isCodeFirst() {
        return codeFirst;
    }
}
