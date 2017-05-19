// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.loaders.impl.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import org.febit.wit.loaders.Resource;

/**
 *
 * @author zqq90
 */
public class FileResource implements Resource {

    protected final File file;
    protected final String encoding;
    protected final boolean codeFirst;
    protected long lastModified;

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
    public boolean isModified() {
        return lastModified != file.lastModified();
    }

    @Override
    public Reader openReader() throws IOException {
        lastModified = file.lastModified();
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
