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

import org.febit.wit.util.PathTrie.Segment;
import org.junit.jupiter.api.Test;

import static org.febit.wit.util.PathTrie.segments;
import static org.junit.jupiter.api.Assertions.*;

class PathTrieTest {

    static PathTrie.Segment[] arr(String... parts) {
        var arr = new Segment[parts.length];
        for (int i = 0; i < parts.length; i++) {
            arr[i] = new Segment(parts[i], 0, parts[i].length());
        }
        return arr;
    }

    @Test
    void testSegmentsDot() {
        assertArrayEquals(arr(), segments("", '.'));

        assertArrayEquals(arr("."), segments(".", '.'));
        assertArrayEquals(arr(".", "."), segments("..", '.'));
        assertArrayEquals(arr(".", ".", "."), segments("...", '.'));

        assertArrayEquals(arr("a"), segments("a", '.'));
        assertArrayEquals(arr("a", ".b"), segments("a.b", '.'));
        assertArrayEquals(arr("a", ".b", ".c"), segments("a.b.c", '.'));
        assertArrayEquals(arr(".a", ".b", ".c", "."), segments(".a.b.c.", '.'));
    }

    @Test
    void testSegmentsSlash() {
        assertArrayEquals(arr(), segments("", '/'));

        assertArrayEquals(arr("/"), segments("/", '/'));
        assertArrayEquals(arr("/", "/"), segments("//", '/'));
        assertArrayEquals(arr("/", "/", "/"), segments("///", '/'));

        assertArrayEquals(arr("a"), segments("a", '/'));
        assertArrayEquals(arr("a", "/b"), segments("a/b", '/'));
        assertArrayEquals(arr("a", "/b", "/c"), segments("a/b/c", '/'));
        assertArrayEquals(arr("/a", "/b", "/c", "/"), segments("/a/b/c/", '/'));
    }

    @Test
    void basicAllowDeny() {
        var trie = PathTrie.builder('.')
                .allow("a.b")
                .deny("a.b.c")

                .allow("x.y")
                .deny("x.y.")

                .allow("m")
                .deny("m.n")
                .allow("m.n.")

                .build();

        assertTrue(trie.match("a.b"));
        assertTrue(trie.match("a.b.x"));
        assertFalse(trie.match("a.b.c"));
        assertFalse(trie.match("a.b.c.x"));

        assertTrue(trie.match("x.y"));
        assertFalse(trie.match("x.y."));
        assertFalse(trie.match("x.y.z"));

        assertTrue(trie.match("m"));
        assertTrue(trie.match("m.x"));
        assertFalse(trie.match("m.n"));
        assertTrue(trie.match("m.n."));
        assertTrue(trie.match("m.n.x"));
    }

    @Test
    void rootRule() {
        var allowAll = PathTrie.builder('.')
                .rule("", true)
                .build();
        var denyAll = PathTrie.builder('.')
                .rule("", false)
                .build();

        assertTrue(allowAll.match("."));
        assertTrue(allowAll.match("anything"));
        assertTrue(allowAll.match("a.b.c"));

        assertFalse(denyAll.match("."));
        assertFalse(denyAll.match("anything"));
        assertFalse(denyAll.match("a.b.c"));
    }

    @Test
    void exactVsPrefix() {
        var trie = PathTrie.builder('/')
                .allow("/a/")
                .allow("/b")
                .build();

        // /a/ allows prefix only
        assertFalse(trie.match("/a"));
        assertTrue(trie.match("/a/"));
        assertTrue(trie.match("/a/x"));

        // /b allows exact and prefix
        assertTrue(trie.match("/b"));
        assertTrue(trie.match("/b/x"));

        // No match
        assertFalse(trie.match("/c"));
        assertFalse(trie.match("/ab"));
    }

    @Test
    void denyOverridesAllow() {
        var trie = PathTrie.builder('.')
                .allow("a")
                .deny("a.b")
                .allow("a.b.c")
                .build();

        assertTrue(trie.match("a"));
        assertTrue(trie.match("a.x"));
        assertFalse(trie.match("a.b"));
        assertFalse(trie.match("a.b.x"));
        assertTrue(trie.match("a.b.c"));
        assertTrue(trie.match("a.b.c.x"));
    }

    @Test
    void emptyPath() {
        var trie = PathTrie.builder('.')
                .allow("a")
                .build();

        assertFalse(trie.match(""));
    }

    @Test
    void slashSeparator() {
        var trie = PathTrie.builder('/')
                .allow("/home/user")
                .deny("/home/user/secret")
                .build();

        assertTrue(trie.match("/home/user"));
        assertTrue(trie.match("/home/user/docs"));
        assertFalse(trie.match("/home/user/secret"));
        assertFalse(trie.match("/home/user/secret/file"));
    }

    @Test
    void separatorFallback() {
        var trie = PathTrie.builder('/')
                .deny("")
                .allow("/")
                .allow("a")
                .deny("/a")
                .allow("/b")
                .build();

        assertFalse(trie.match(""));
        assertTrue(trie.match("/"));

        assertTrue(trie.match("a"));
        assertFalse(trie.match("b"));
        assertFalse(trie.match("c"));

        assertFalse(trie.match("/a"));
        assertTrue(trie.match("/b"));
        assertTrue(trie.match("/c"));
    }

    @Test
    void dotSeparator() {
        var trie = PathTrie.builder('.')
                .allow("java.lang")
                .deny("java.lang.System")
                .build();

        assertTrue(trie.match("java.lang"));
        assertTrue(trie.match("java.lang.String"));
        assertFalse(trie.match("java.lang.System"));
        assertFalse(trie.match("java.lang.System.exit"));
    }

    @Test
    void noMatchingRule() {
        var trie = PathTrie.builder('.')
                .allow("a.b")
                .build();

        assertFalse(trie.match("x"));
        assertFalse(trie.match("a.c"));
        assertFalse(trie.match("a.bc"));
    }

    @Test
    void complexHierarchy() {
        var trie = PathTrie.builder('.')
                .rule("", false)
                .allow("com")
                .allow("com.example")
                .deny("com.example.internal")
                .allow("com.example.internal.util")
                .build();

        assertFalse(trie.match("org"));
        assertTrue(trie.match("com"));
        assertTrue(trie.match("com.other"));
        assertTrue(trie.match("com.example"));
        assertTrue(trie.match("com.example.api"));
        assertFalse(trie.match("com.example.internal"));
        assertFalse(trie.match("com.example.internal.api"));
        assertTrue(trie.match("com.example.internal.util"));
        assertTrue(trie.match("com.example.internal.util.Helper"));
    }
}
