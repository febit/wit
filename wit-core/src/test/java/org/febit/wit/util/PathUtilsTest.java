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
package org.febit.wit.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PathUtilsTest {

    @Test
    void concat() {

        // Null base path
        assertNull(PathUtils.concat(null, "tmpl.wit"));
        assertNull(PathUtils.concat(null, "./tmpl.wit"));
        assertNull(PathUtils.concat(null, "../tmpl.wit"));
        assertNull(PathUtils.concat(null, "/tmpl.wit"));

        // Overflowing above root
        assertNull(PathUtils.concat("/", "../tmpl.wit"));
        assertNull(PathUtils.concat("", "../tmpl.wit"));

        // Invalid
        assertNull(PathUtils.concat("/parent/", ":tmpl.wit"));

        // Just return normalized
        assertEquals("tmpl.wit", PathUtils.concat("", "./tmpl.wit"));
        assertEquals("tmpl.wit", PathUtils.concat("", "././tmpl.wit"));
        assertEquals("tmpl.wit", PathUtils.concat("", "a/../tmpl.wit"));

        assertEquals("/parent/tmpl.wit", PathUtils.concat("/parent/", "tmpl.wit"));
        assertEquals("/parent/tmpl.wit", PathUtils.concat("/parent/", "./tmpl.wit"));
        assertEquals("/tmpl.wit", PathUtils.concat("/parent/", "../tmpl.wit"));

        assertEquals("/parent/tmpl.wit", PathUtils.concat("/parent", "tmpl.wit"));
        assertEquals("/parent/tmpl.wit", PathUtils.concat("/parent", "./tmpl.wit"));
        assertEquals("/tmpl.wit", PathUtils.concat("/parent", "../tmpl.wit"));
    }

    @Test
    void normalize() {
        // Overflowing above root
        assertNull(PathUtils.normalize("/../tmpl.wit"));
        assertNull(PathUtils.normalize("../tmpl.wit"));
        assertNull(PathUtils.normalize("/parent/../../tmpl.wit"));

        // Invalid
        assertNull(PathUtils.normalize(":tmpl.wit"));

        // Empty
        assertNull(PathUtils.normalize(null));
        assertEquals("", PathUtils.normalize(""));

        // Window separators
        assertEquals("a/b/c", PathUtils.normalize("a\\b\\c"));
        assertEquals("/path/tmpl.wit", PathUtils.normalize("/path\\to/\\..\\./.\\tmpl.wit"));

        // Special segments
        assertEquals("~/", PathUtils.normalize("~"));
        assertEquals("~/", PathUtils.normalize("~/"));
        assertEquals("~/tmpl.wit", PathUtils.normalize("~/tmpl.wit"));

        assertNull(PathUtils.normalize("~/../tmpl.wit"));

        // Slashes
        assertEquals("/path/tmpl.wit", PathUtils.normalize("/path//to/..//tmpl.wit"));
        assertEquals("/path/tmpl.wit", PathUtils.normalize("/path/to/.././/tmpl.wit"));
        assertEquals("/path/tmpl.wit", PathUtils.normalize("/path/./to/../tmpl.wit"));
        assertEquals("/path/to..tmpl.wit", PathUtils.normalize("/path/./to..tmpl.wit"));
        assertEquals("/path/..tmpl.wit", PathUtils.normalize("/path/./..tmpl.wit"));
    }

    @Test
    void sibling() {
        assertEquals("/parent/sibling.wit", PathUtils.sibling("/parent/tmpl.wit", "sibling.wit"));
        assertEquals("/parent/sibling.wit", PathUtils.sibling("/parent/tmpl.wit", "./sibling.wit"));
        assertEquals("/sibling.wit", PathUtils.sibling("/parent/tmpl.wit", "../sibling.wit"));

        assertEquals("/sibling.wit", PathUtils.sibling("/parent/tmpl.wit", "/sibling.wit"));

        assertEquals("sibling.wit", PathUtils.sibling(null, "sibling.wit"));
        assertEquals("./sibling.wit", PathUtils.sibling(null, "./sibling.wit"));
        assertEquals("../sibling.wit", PathUtils.sibling(null, "../sibling.wit"));
    }

    @Test
    void parent() {
        assertNull(PathUtils.parent(null));
        assertEquals("", PathUtils.parent(""));

        // Non-UNIX separators are not treated as separators.
        assertEquals("", PathUtils.parent("a\\b\\c"));

        assertEquals("", PathUtils.parent("abc"));
        assertEquals("", PathUtils.parent("tmpl.wit"));

        assertEquals("/", PathUtils.parent("/tmpl.wit"));
        assertEquals("parent/", PathUtils.parent("parent/tmpl.wit"));
        assertEquals("/parent/", PathUtils.parent("/parent/tmpl.wit"));
    }

    @Test
    void getPrefixLength() {
        // Invalid
        assertEquals(-1, PathUtils.getPrefixLength(null));
        assertEquals(-1, PathUtils.getPrefixLength(":abc"));

        // No prefix
        assertEquals(0, PathUtils.getPrefixLength(""));
        assertEquals(0, PathUtils.getPrefixLength("a"));
        assertEquals(0, PathUtils.getPrefixLength("abc"));
        assertEquals(0, PathUtils.getPrefixLength("./abc"));
        assertEquals(0, PathUtils.getPrefixLength("../abc"));

        assertEquals(1, PathUtils.getPrefixLength("/abc"));
        assertEquals(1, PathUtils.getPrefixLength("/"));
        assertEquals(1, PathUtils.getPrefixLength("/abc/def"));

        // Unix home directory prefix
        assertEquals(2, PathUtils.getPrefixLength("~"));
        assertEquals(2, PathUtils.getPrefixLength("~/abc"));
        assertEquals(6, PathUtils.getPrefixLength("~\\abc"));
        assertEquals(6, PathUtils.getPrefixLength("~user"));
        assertEquals(6, PathUtils.getPrefixLength("~user/abc"));
        assertEquals(10, PathUtils.getPrefixLength("~user\\abc"));
        assertEquals(3, PathUtils.getPrefixLength("~:/abc"));
    }

    @Test
    void getPrefixLengthWindows() {
        // Windows drive letter prefix
        assertEquals(2, PathUtils.getPrefixLength("C:"));
        assertEquals(3, PathUtils.getPrefixLength("C:/"));
        assertEquals(3, PathUtils.getPrefixLength("C:\\"));
        assertEquals(2, PathUtils.getPrefixLength("C:abc"));
        assertEquals(3, PathUtils.getPrefixLength("C:/abc"));
        assertEquals(3, PathUtils.getPrefixLength("C:\\abc"));
        assertEquals(3, PathUtils.getPrefixLength("x:/abc"));
        assertEquals(-1, PathUtils.getPrefixLength("1:/abc"));
        assertEquals(-1, PathUtils.getPrefixLength("1:\\abc"));
        assertEquals(-1, PathUtils.getPrefixLength("\r:/abc"));
        assertEquals(-1, PathUtils.getPrefixLength("_:/abc"));
        assertEquals(-1, PathUtils.getPrefixLength("?:/abc"));
        assertEquals(-1, PathUtils.getPrefixLength("|:/abc"));

        // Windows UNC path prefix
        assertEquals(9, PathUtils.getPrefixLength("//server/share"));
        assertEquals(9, PathUtils.getPrefixLength("\\\\server\\share"));
    }

}
