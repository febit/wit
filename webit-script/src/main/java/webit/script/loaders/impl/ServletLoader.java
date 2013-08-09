package webit.script.loaders.impl;

import javax.servlet.ServletContext;
import webit.script.exceptions.ResourceNotFoundException;
import webit.script.loaders.AbstractLoader;
import webit.script.loaders.Resource;
import webit.script.loaders.impl.resources.FileResource;

/**
 *
 * @author Zqq
 */
public class ServletLoader extends AbstractLoader {

    private ServletContext servletContext;

    public Resource get(String name) throws ResourceNotFoundException {
        String realpath = servletContext.getRealPath(getRealPath(name));
        return new FileResource(realpath, encoding);
    }
}
