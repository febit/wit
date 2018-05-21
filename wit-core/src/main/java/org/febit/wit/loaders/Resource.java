// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.loaders;

import java.io.IOException;
import java.io.Reader;

public interface Resource {

    /**
     * if exists this resource.
     *
     * @return boolean
     * @since 1.4.1
     */
    boolean exists();

    /**
     * if this resource is begin with code.
     *
     * @return boolean
     * @since 2.0.0
     */
    boolean isCodeFirst();

    Reader openReader() throws IOException;

    /**
     * Return current resource version.
     *
     * @return current resource version.
     * @since 2.7.0
     */
    long version();
}
