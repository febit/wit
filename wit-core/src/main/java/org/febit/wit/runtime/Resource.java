// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime;

import java.io.IOException;
import java.io.Reader;

public interface Resource {

    /**
     * if exists this resource.
     *
     * @return boolean
     */
    boolean exists();

    /**
     * if this resource is begin with code.
     *
     * @return boolean
     */
    boolean codeFirst();

    Reader openReader() throws IOException;

    /**
     * Return current resource version.
     *
     * @return current resource version.
     */
    long version();

    default int getOffsetLine() {
        return 0;
    }

    default int getOffsetColumnOfFirstLine() {
        return 0;
    }
}
