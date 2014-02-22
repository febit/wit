// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.loaders.impl.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import webit.script.exceptions.ResourceNotFoundException;
import webit.script.loaders.Resource;
import webit.script.util.ClassLoaderUtil;
import webit.script.util.StreamUtil;

/**
 *
 * @author Zqq
 */
public class ClasspathResource implements Resource {

    private final String path;
    private final String encoding;
    //private long lastLoadTime;

    public ClasspathResource(String path, String encoding) {
        this.path = path;
        this.encoding = encoding;
    }

    /**
     * @since 1.4.1
     */
    public boolean exists() {
        final InputStream inputStream;
        if ((inputStream = ClassLoaderUtil.getDefaultClassLoader().getResourceAsStream(path)) != null) {
            StreamUtil.close(inputStream);
            return true;
        }
        return false;
    }

    public boolean isModified() {
        return false;
    }

    public Reader openReader() throws IOException {
        //lastLoadTime = System.currentTimeMillis();
        final InputStream inputStream;
        if ((inputStream = ClassLoaderUtil.getDefaultClassLoader().getResourceAsStream(path)) != null) {
            return encoding == null
                    ? new InputStreamReader(inputStream)
                    : new InputStreamReader(inputStream, encoding);
        } else {
            throw new ResourceNotFoundException("Resource Not Found: ".concat(path));
        }
    }
}
