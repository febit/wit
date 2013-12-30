// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.web.loaders;

import javax.servlet.ServletContext;
import webit.script.CFG;
import webit.script.Engine;
import webit.script.loaders.AbstractLoader;
import webit.script.loaders.Resource;
import webit.script.loaders.impl.resources.FileResource;

/**
 *
 * @author Zqq
 */
public class ServletLoader extends AbstractLoader{

    private ServletContext servletContext;

    @Override
    public void init(Engine engine) {
        super.init(engine);
        if (this.servletContext == null) {
            this.servletContext = (ServletContext) engine.getConfig(CFG.SERVLET_CONTEXT);
        }
    }

    public Resource get(String path) {
        path = getRealPath(path);
        final String filepath;
        if ((filepath = servletContext.getRealPath(path)) != null) {
            return new FileResource(filepath, encoding);
        } else {
            return new ServletContextResource(path, encoding, servletContext);
        }
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
