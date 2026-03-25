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

import io.roastedroot.zerofs.Configuration;
import io.roastedroot.zerofs.ZeroFs;
import org.apache.commons.io.IOUtils;
import org.febit.wit.io.Loader;
import org.febit.wit.io.Loaders;
import org.febit.wit.io.Source;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FileSystemLoaderTest {

    final Map<String, String> tmpls = Map.of(
            "/a/b/c.wit", "abc",
            "/a/b/d.wit2", "abd",
            "/a/e/f.wit", "aef"
    );

    final FileSystem fs = prepareFs();
    final Loader loader = Loaders.fileSystem()
            .fileSystem(fs)
            .charset(StandardCharsets.UTF_8)
            .beginWith(Source.BeginWith.SCRIPT)
            .root("/a")
            .completeMissingSuffix(".wit")
            .candidateSuffixes(List.of(".wit2"))
            .build();

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

        assertEquals("/", loader.normalize(""));
        assertEquals("/a/b/c.wit", loader.normalize("a/b/c/"));

        assertEquals("/c.wit", loader.normalize("\\c.wit"));
        assertEquals("/c.wit", loader.normalize("/c.wit"));
        assertEquals("/c.wit", loader.normalize("c.wit"));

        assertEquals("/a/b/c.wit", loader.normalize("/a/b/c.wit"));
        assertEquals("/a/b/c.wit", loader.normalize("/a/b/c"));
        assertEquals("/a/b/d.wit2", loader.normalize("/a/b/d.wit2"));
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
    void version() throws IOException {
        var source = loader.get("/b/c.wit");
        assertEquals(Files.getLastModifiedTime(fs.getPath("/a/b/c.wit")).toMillis(), source.version());

        source = loader.get("/b/not-exists.wit");
        assertEquals(-1L, source.version());
    }

    @Test
    void content() throws IOException {
        var source = loader.get("/b/c.wit");
        assertNotNull(source);
        assertTrue(source.exists());
        assertEquals("abc", IOUtils.toString(source.open()));

        source = loader.get("/b/d.wit2");
        assertNotNull(source);
        assertTrue(source.exists());
        assertEquals("abd", IOUtils.toString(source.open()));

        source = loader.get("/e/f.wit");
        assertNotNull(source);
        assertTrue(source.exists());
        assertEquals("aef", IOUtils.toString(source.open()));
    }

    @Test
    void absPathIsNotAvailable() {
        var source = loader.get("/a/b/c.wit");
        assertNotNull(source);
        assertFalse(source.exists());
    }

    FileSystem prepareFs() {
        var fs = ZeroFs.newFileSystem(Configuration.unix());
        tmpls.forEach((path, content) -> {
            var p = fs.getPath(path);
            try {
                Files.createDirectories(p.getParent());
                Files.writeString(p, content, StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return fs;
    }

}
