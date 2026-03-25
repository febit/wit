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
import org.febit.wit.exception.NoSuchSourceException;
import org.febit.wit.io.Loader;
import org.febit.wit.io.Source;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ClasspathLoaderTest {

    final Map<String, String> tmpls = Map.of(
            "/a/b/c.wit", "abc",
            "/a/b/d.wit2", "abd",
            "/a/e/f.wit", "aef"
    );

    final Loader loader = ClasspathLoader.of(prepareClassLoader(), StandardCharsets.UTF_8, Source.BeginWith.SCRIPT);

    @Test
    void invalidPath() {
        var source = loader.get("../../");
        assertNotNull(source);
        assertFalse(source.exists());
    }

    @Test
    void normalize() {
        assertNull(loader.normalize(null));
        assertNull(loader.normalize("/a/../../c.wit"));

        assertEquals("/c.wit", loader.normalize("\\c.wit"));
        assertEquals("/c.wit", loader.normalize("/c.wit"));
        assertEquals("c.wit", loader.normalize("c.wit"));
    }

    @Test
    void content() throws IOException {
        var source = loader.get("/a/b/c.wit");
        assertNotNull(source);
        assertTrue(source.exists());
        assertEquals("abc", IOUtils.toString(source.open()));

        source = loader.get("/a/b/not-exists.wit");
        assertNotNull(source);
        assertFalse(source.exists());
        assertThrows(NoSuchSourceException.class, source::open);

        source = loader.get("/a/b/d.wit2");
        assertNotNull(source);
        assertTrue(source.exists());
        assertEquals("abd", IOUtils.toString(source.open()));

        source = loader.get("/a/e/f.wit");
        assertNotNull(source);
        assertTrue(source.exists());
        assertEquals("aef", IOUtils.toString(source.open()));
    }

    @Test
    void sibling() {
        assertNull(loader.sibling(null, null));
        assertNull(loader.sibling("/a/b/c.wit", null));
        assertEquals("a.wit", loader.sibling(null, "a.wit"));

        assertEquals("/a/b/d", loader.sibling("/a/b/c.wit", "d"));
        assertEquals("/d.wit2", loader.sibling("/a/b/c.wit", "/d.wit2"));
    }

    @Test
    void version() {
        var source = loader.get("/a/b/c.wit");
        assertEquals(0L, source.version());

        source = loader.get("/a/b/not-exists.wit");
        assertEquals(0L, source.version());
    }

    ClassLoader prepareClassLoader() {
        var loader = mock(ClassLoader.class);
        when(loader.getResourceAsStream(anyString())).thenAnswer(inv -> {
            var path = inv.getArgument(0, String.class);
            var content = tmpls.get(path);
            if (content == null) {
                return null;
            }
            return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        });
        when(loader.getResource(anyString())).thenAnswer(inv -> {
            var path = inv.getArgument(0, String.class);
            var content = tmpls.get(path);
            if (content == null) {
                return null;
            }
            return URI.create("file://fake/" + path).toURL();
        });
        return loader;
    }
}
