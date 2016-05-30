// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.loaders.impl.resources;

import java.io.IOException;
import java.io.Reader;
import webit.script.exceptions.ResourceNotFoundException;
import webit.script.loaders.Resource;

/**
 * @version 1.5.0
 * @author Zqq
 */
public class NotFoundResource implements Resource {

    protected String name;
    protected String message;

    public NotFoundResource(String name, String message) {
        this.name = name;
        this.message = message;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public Reader openReader() throws IOException {
        throw new ResourceNotFoundException(message);
    }
}
