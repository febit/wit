// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.servlet.loaders;

import org.febit.wit.lang.Resource;
import org.febit.wit.loaders.AbstractLoader;
import org.febit.wit.loaders.impl.resources.FileResource;

import javax.servlet.ServletContext;

/**
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
        return new ServletContextResource(path, encoding, servletContext, codeFirst);
    }
}
