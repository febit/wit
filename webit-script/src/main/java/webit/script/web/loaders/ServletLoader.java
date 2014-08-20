// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.web.loaders;

import javax.servlet.ServletContext;
import webit.script.loaders.AbstractLoader;
import webit.script.loaders.Resource;
import webit.script.loaders.impl.resources.FileResource;

/**
 *
 * @author Zqq
 */
public class ServletLoader extends AbstractLoader{

    private ServletContext servletContext;

    public Resource get(String path) {
        path = getRealPath(path);
        final String filepath = servletContext.getRealPath(path);
        if (filepath != null) {
            return new FileResource(filepath, encoding);
        } else {
            return new ServletContextResource(path, encoding, servletContext);
        }
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
