// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.web.loaders;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.servlet.ServletContext;
import webit.script.exceptions.ResourceNotFoundException;
import webit.script.loaders.Resource;

/**
 *
 * @author zqq90 <zqq_90@163.com>
 */
public class ServletContextResource implements Resource {

    private final String path;
    private final String encoding;
    private final ServletContext servletContext;

    public ServletContextResource(String path, String encoding, ServletContext servletContext) {
        this.path = path;
        this.encoding = encoding;
        this.servletContext = servletContext;
    }

    public boolean isModified() {
        return false;
    }

    public Reader openReader() throws IOException {
        final InputStream in;
        if ((in = servletContext.getResourceAsStream(path)) != null) {
            return new InputStreamReader(in, encoding);
        } else {
            throw new ResourceNotFoundException("Resource Not Found: ".concat(path));
        }
    }
}
