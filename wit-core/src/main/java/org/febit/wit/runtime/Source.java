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
     * Source begin with script or template.
     *
     * @return BeginWith
     */
    BeginWith beginWith();

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

    enum BeginWith {
        SCRIPT,
        TEMPLATE
    }
}
