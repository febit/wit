// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.servlet.loaders;

import javax.servlet.ServletContext;
import webit.script.loaders.AbstractLoader;
import webit.script.loaders.Resource;
import webit.script.loaders.impl.resources.FileResource;

/**
 *
 * @author zqq90
 */
public class ServletLoader extends AbstractLoader {

    protected ServletContext servletContext;

    @Override
    public Resource get(String path) {
        path = getRealPath(path);
        final String filepath = servletContext.getRealPath(path);
        if (filepath != null) {
            return new FileResource(filepath, encoding);
        }
        return new ServletContextResource(path, encoding, servletContext);
    }
}
