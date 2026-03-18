/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.febit.wit.io;

import edu.umd.cs.findbugs.annotations.CheckReturnValue;

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

    @CheckReturnValue
    Reader open() throws IOException;

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
