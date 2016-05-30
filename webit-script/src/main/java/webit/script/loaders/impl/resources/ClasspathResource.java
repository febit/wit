// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.loaders.impl.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import webit.script.exceptions.ResourceNotFoundException;
import webit.script.loaders.Resource;
import webit.script.util.ClassUtil;

/**
 *
 * @author zqq90
 */
public class ClasspathResource implements Resource {

    private final String path;
    private final String encoding;

    public ClasspathResource(String path, String encoding) {
        this.path = path;
        this.encoding = encoding;
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
            return encoding == null
                    ? new InputStreamReader(in)
                    : new InputStreamReader(in, encoding);
        } else {
            throw new ResourceNotFoundException("Resource Not Found: ".concat(path));
        }
    }
}
