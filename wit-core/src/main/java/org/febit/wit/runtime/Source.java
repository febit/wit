// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime;

import java.io.IOException;
import java.io.Reader;

public interface Source {

    /**
     * if exists this source.
     *
     * @return boolean
     */
    boolean exists();

    /**
     * if this source is begin with code.
     *
     * @return boolean
     */
    boolean codeFirst();

    Reader openReader() throws IOException;

    /**
     * Return current source version.
     *
     * @return current source version.
     */
    long version();

    default int getOffsetLine() {
        return 0;
    }

    default int getOffsetColumnOfFirstLine() {
        return 0;
    }
}
