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

import org.febit.wit.io.Loader;
import org.febit.wit.io.Loaders;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SecurityLoaderTest {

    static void assertAllow(String path, Loader loader) {
        assertInstanceOf(StringSource.class, loader.get(path), "Expected allow: " + path);
    }

    static void assertDeny(String path, Loader loader) {
        assertInstanceOf(EmptySource.class, loader.get(path), "Expected deny: " + path);
    }

    @Test
    void builderAllowAndDeny() {
        var loader = Loaders.security(Loaders.string().build())
                .allow("/a/", "/b")
                .deny("/b/secret")
                .allow("/c/d")
                .build();

        assertAllow("/a/x.wit", loader);
        assertAllow("/b", loader);
        assertAllow("/b/x.wit", loader);
        assertAllow("/c/d", loader);

        assertDeny("/b/secret", loader);
        assertDeny("/b/secret/x.wit", loader);
    }

    @Test
    void denyOverridesAllow() {
        var loader = SecurityLoader.builder(Loaders.string().build())
                .allow("/a/")
                .deny("/a/b/")
                .allow("/a/b/c")
                .build();

        assertAllow("/a/x.wit", loader);
        assertDeny("/a/b/x.wit", loader);
        assertAllow("/a/b/c", loader);
    }

    @Test
    void directoryVsExact() {
        Loader delegate = Loaders.string().build();
        var loader = SecurityLoader.builder(delegate).allow(List.of("/foo", "/bar/")).build();

        assertAllow("/foo", loader);
        assertAllow("/foo/", loader);
        assertAllow("/foo/x.wit", loader);

        assertDeny("/foobar", loader);

        assertDeny("/bar", loader);
        assertAllow("/bar/", loader);
        assertAllow("/bar/x.wit", loader);
    }

    @Test
    void normalizedPaths() {
        Loader delegate = Loaders.string().build();
        var loader = SecurityLoader.builder(delegate).allow(List.of("/a/", "/b")).build();

        assertAllow("/a//x.wit", loader);
        assertAllow("/b//x.wit", loader);

        assertDeny("/a/../c.wit", loader);
        assertDeny("/b/../c.wit", loader);
        assertDeny("//b", loader);
    }

    @Test
    void emptyRules() {
        Loader delegate = Loaders.string().build();
        var loader = SecurityLoader.builder(delegate).allow(List.of()).build();

        assertDeny("/anything", loader);
    }

    @Test
    void invalidPaths() {
        var loader = Loaders.security(Loaders.string().build())
                .allow("/safe/")
                .build();

        assertDeny("/safe/../../etc/passwd", loader);
        assertDeny("/safe/../x", loader);
    }
}
