// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.loaders;

import java.io.IOException;
import java.io.Reader;

public interface Resource {

    boolean isModified();

    /**
     * if exists this resource.
     *
     * @return boolean
     * @since 1.4.1
     */
    boolean exists();

    Reader openReader() throws IOException;
}
