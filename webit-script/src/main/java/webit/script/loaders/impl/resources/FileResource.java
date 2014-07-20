// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.loaders.impl.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import webit.script.loaders.Resource;

/**
 *
 * @author Zqq
 */
public class FileResource implements Resource {

    private final File file;
    private final String encoding;
    private long lastModified;

    public FileResource(String path, String encoding) {
        this.encoding = encoding;
        this.file = new File(path);
    }

    public boolean isModified() {
        return lastModified != file.lastModified();
    }

    public Reader openReader() throws IOException {
        lastModified = file.lastModified();
        return new InputStreamReader(
                new FileInputStream(file),
                encoding);
    }

    /**
     * @since 1.4.1
     */
    public boolean exists() {
        return file.exists();
    }
}
