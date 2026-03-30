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

import static org.febit.wit.util.PathUtils.concat;
import static org.febit.wit.util.PathUtils.getPrefixLength;
import static org.febit.wit.util.PathUtils.normalize;
import static org.febit.wit.util.PathUtils.parent;
import static org.febit.wit.util.PathUtils.sibling;
import static org.junit.jupiter.api.Assertions.*;

class PathUtilsTest {

    @Test
    void testConcat() {

        // Null base path
        assertNull(concat(null, "tmpl.wit"));
        assertNull(concat(null, "./tmpl.wit"));
        assertNull(concat(null, "../tmpl.wit"));
        assertNull(concat(null, "/tmpl.wit"));

        // Overflowing above root
        assertNull(concat("/", "../tmpl.wit"));
        assertNull(concat("", "../tmpl.wit"));

        // Invalid
        assertNull(concat("/parent/", ":tmpl.wit"));

        // Just return normalized
        assertEquals("tmpl.wit", concat("", "./tmpl.wit"));
        assertEquals("tmpl.wit", concat("", "././tmpl.wit"));
        assertEquals("tmpl.wit", concat("", "a/../tmpl.wit"));

        assertEquals("/parent/tmpl.wit", concat("/parent/", "tmpl.wit"));
        assertEquals("/parent/tmpl.wit", concat("/parent/", "./tmpl.wit"));
        assertEquals("/tmpl.wit", concat("/parent/", "../tmpl.wit"));

        assertEquals("/parent/tmpl.wit", concat("/parent", "tmpl.wit"));
        assertEquals("/parent/tmpl.wit", concat("/parent", "./tmpl.wit"));
        assertEquals("/tmpl.wit", concat("/parent", "../tmpl.wit"));
    }

    @Test
    void testNormalize() {
        // Overflowing above root
        assertNull(normalize(".."));
        assertNull(normalize("../"));
        assertNull(normalize("../tmpl.wit"));
        assertNull(normalize("/../"));
        assertNull(normalize("/../a"));
        assertNull(normalize("/../tmpl.wit"));
        assertNull(normalize("/parent/../../tmpl.wit"));

        // Dot
        assertEquals("", normalize("."));
        assertEquals("", normalize("./"));
        assertEquals("", normalize("././"));
        assertEquals("a", normalize("./a"));
        assertEquals("a", normalize("./a"));
        assertEquals("a", normalize("././a"));

        // Invalid
        assertNull(normalize(":tmpl.wit"));

        // Empty
        assertNull(normalize(null));
        assertEquals("", normalize(""));

        assertEquals("/a/", normalize("/a/"));
        assertEquals("/a", normalize("/a"));

        // Window separators
        assertEquals("a/b/c", normalize("a\\b\\c"));
        assertEquals("/path/tmpl.wit", normalize("/path\\to/\\..\\./.\\tmpl.wit"));

        // Special segments
        assertEquals("~/", normalize("~"));
        assertEquals("~/", normalize("~/"));
        assertEquals("~/tmpl.wit", normalize("~/tmpl.wit"));

        assertNull(normalize("~/../tmpl.wit"));
    }

    @Test
    void testNormalizeSlashes() {
        assertEquals("/path/tmpl.wit", normalize("/path//to/..//tmpl.wit"));
        assertEquals("/path/tmpl.wit", normalize("/path/to/.././/tmpl.wit"));
        assertEquals("/path/tmpl.wit", normalize("/path/./to/../tmpl.wit"));
        assertEquals("/path/to..tmpl.wit", normalize("/path/./to..tmpl.wit"));
        assertEquals("/path/..tmpl.wit", normalize("/path/./..tmpl.wit"));
    }

    @Test
    void testNormalizeTrailingSlash() {
        assertEquals("/", normalize("/a/.."));
        assertEquals("/", normalize("/a/../"));
        assertEquals("/a/", normalize("/a/"));
        assertEquals("/a", normalize("/a"));
        assertEquals("/a", normalize("/a/."));
        assertEquals("/a/", normalize("/a/./"));
        assertEquals("/a", normalize("/a/b/.."));
        assertEquals("/a/", normalize("/a/b/../"));
        assertEquals("/a/b", normalize("/a/b/."));
        assertEquals("/a/b/", normalize("/a/b/./"));
    }

    @Test
    void testSibling() {
        assertEquals("/parent/sibling.wit", sibling("/parent/tmpl.wit", "sibling.wit"));
        assertEquals("/parent/sibling.wit", sibling("/parent/tmpl.wit", "./sibling.wit"));
        assertEquals("/sibling.wit", sibling("/parent/tmpl.wit", "../sibling.wit"));

        assertEquals("/sibling.wit", sibling("/parent/tmpl.wit", "/sibling.wit"));

        assertEquals("sibling.wit", sibling(null, "sibling.wit"));
        assertEquals("./sibling.wit", sibling(null, "./sibling.wit"));
        assertEquals("../sibling.wit", sibling(null, "../sibling.wit"));
    }

    @Test
    void testParent() {
        assertNull(parent(null));
        assertEquals("", parent(""));

        // Non-UNIX separators are not treated as separators.
        assertEquals("", parent("a\\b\\c"));

        assertEquals("", parent("abc"));
        assertEquals("", parent("tmpl.wit"));

        assertEquals("/", parent("/tmpl.wit"));
        assertEquals("parent/", parent("parent/tmpl.wit"));
        assertEquals("/parent/", parent("/parent/tmpl.wit"));
    }

    @Test
    void testGetPrefixLength() {
        // Invalid
        assertEquals(-1, getPrefixLength(null));
        assertEquals(-1, getPrefixLength(":abc"));

        // No prefix
        assertEquals(0, getPrefixLength(""));
        assertEquals(0, getPrefixLength("a"));
        assertEquals(0, getPrefixLength("abc"));
        assertEquals(0, getPrefixLength("./abc"));
        assertEquals(0, getPrefixLength("../abc"));

        assertEquals(1, getPrefixLength("/abc"));
        assertEquals(1, getPrefixLength("/"));
        assertEquals(1, getPrefixLength("/abc/def"));

        // Unix home directory prefix
        assertEquals(2, getPrefixLength("~"));
        assertEquals(2, getPrefixLength("~/abc"));
        assertEquals(6, getPrefixLength("~\\abc"));
        assertEquals(6, getPrefixLength("~user"));
        assertEquals(6, getPrefixLength("~user/abc"));
        assertEquals(10, getPrefixLength("~user\\abc"));
        assertEquals(3, getPrefixLength("~:/abc"));
    }

    @Test
    void testGetPrefixLengthWindows() {
        // Windows drive letter prefix
        assertEquals(2, getPrefixLength("C:"));
        assertEquals(3, getPrefixLength("C:/"));
        assertEquals(3, getPrefixLength("C:\\"));
        assertEquals(2, getPrefixLength("C:abc"));
        assertEquals(3, getPrefixLength("C:/abc"));
        assertEquals(3, getPrefixLength("C:\\abc"));
        assertEquals(3, getPrefixLength("x:/abc"));
        assertEquals(-1, getPrefixLength("1:/abc"));
        assertEquals(-1, getPrefixLength("1:\\abc"));
        assertEquals(-1, getPrefixLength("\r:/abc"));
        assertEquals(-1, getPrefixLength("_:/abc"));
        assertEquals(-1, getPrefixLength("?:/abc"));
        assertEquals(-1, getPrefixLength("|:/abc"));

        // Windows UNC path prefix
        assertEquals(9, getPrefixLength("//server/share"));
        assertEquals(9, getPrefixLength("\\\\server\\share"));
    }

}
