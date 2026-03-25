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

import org.apache.commons.io.IOUtils;
import org.febit.wit.io.Loaders;
import org.febit.wit.io.Source;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class StringLoaderTest {

    @Test
    void get() throws IOException {
        var loader = Loaders.string().build();
        var source = loader.get("code");
        assertNotNull(source);
        assertInstanceOf(StringSource.class, source);

        assertEquals("code", ((StringSource) source).content());
        assertEquals("code", IOUtils.toString(source.open()));

        assertTrue(source.exists());
        assertEquals(0L, source.version());
        assertEquals(Source.BeginWith.SCRIPT, source.beginWith());
    }

    @Test
    void getTemplate() {
        var source = Loaders.string()
                .beginWith(Source.BeginWith.TEMPLATE)
                .build()
                .get("template");
        assertNotNull(source);
        assertEquals("template", ((StringSource) source).content());
        assertEquals(Source.BeginWith.TEMPLATE, source.beginWith());
    }

    @Test
    void sibling() {
        var loader = Loaders.string().build();
        assertEquals("relative", loader.sibling("refer", "relative"));
        assertEquals("relative", loader.sibling(null, "relative"));
    }

    @Test
    void normalize() {
        var loader = Loaders.string().build();
        assertEquals(" path //\\", loader.normalize(" path //\\"));
        assertNull(loader.normalize(null));
    }

    @Test
    void isCacheEnabled() {
        var loader = Loaders.string().build();
        assertFalse(loader.isCacheEnabled("path"));

        loader = Loaders.string()
                .cacheEnabled(true)
                .build();
        assertTrue(loader.isCacheEnabled("path"));
    }
}
