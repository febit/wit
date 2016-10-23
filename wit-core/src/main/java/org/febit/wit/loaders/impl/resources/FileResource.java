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

    private final File file;
    private final String encoding;
    private long lastModified;

    public FileResource(String path, String encoding) {
        this.encoding = encoding;
        this.file = new File(path);
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
}
