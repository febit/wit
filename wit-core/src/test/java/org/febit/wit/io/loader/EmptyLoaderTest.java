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
package org.febit.wit.io.loader;

import org.febit.wit.exception.NoSuchSourceException;
import org.febit.wit.io.Loaders;
import org.febit.wit.io.Source;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmptyLoaderTest {

    private final EmptyLoader loader = Loaders.empty();

    @Test
    void get() {
        var source = loader.get("path");
        assertNotNull(source);
        assertInstanceOf(EmptySource.class, source);
        assertEquals("path", ((EmptySource) source).path());

        assertFalse(source.exists());
        assertEquals(0L, source.version());
        assertThrows(NoSuchSourceException.class, source::open);
        assertEquals(Source.BeginWith.SCRIPT, source.beginWith());
    }

    @Test
    void sibling() {
        assertEquals("relative", loader.sibling("refer", "relative"));
        assertEquals("relative", loader.sibling(null, "relative"));
    }

    @Test
    void normalize() {
        assertEquals("path", loader.normalize("path"));
        assertNull(loader.normalize(null));
    }

    @Test
    void isCacheEnabled() {
        assertFalse(loader.isCacheEnabled("path"));
    }
}
