// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.loaders.impl;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import webit.script.exceptions.ResourceNotFoundException;
import webit.script.loaders.AbstractLoader;
import webit.script.loaders.Resource;
import webit.script.loaders.impl.resources.FileResource;

/**
 *
 * @author Zqq
 */
public class ServletLoader extends AbstractLoader implements ServletContextListener {

    private ServletContext servletContext;

    public Resource get(String name) throws ResourceNotFoundException {
        String realpath = servletContext.getRealPath(getRealPath(name));
        if (realpath == null) {
            throw new ResourceNotFoundException("Not found: " + name);
        }
        return new FileResource(realpath, encoding);
    }

    public void contextInitialized(ServletContextEvent sce) {
        servletContext = sce.getServletContext();
    }

    public void contextDestroyed(ServletContextEvent sce) {
        servletContext = null;
    }
}
