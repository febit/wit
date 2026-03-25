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

import org.febit.wit.io.Source;
import org.febit.wit.util.ClassUtils;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class AdvancePathLoaderDecoratorTest {

    final ClasspathLoader delegate = ClasspathLoader.of(
            ClassUtils.classLoader(),
            StandardCharsets.UTF_8,
            Source.BeginWith.SCRIPT
    );

    @Test
    void basic() {
        var loader = AdvancePathLoaderDecorator.builder()
                .delegate(delegate)
                .cacheEnabled(true)
                .candidateSuffixes(null)
                .build();

        assertTrue(loader.isCacheEnabled("a"));

        var source = loader.get("/a/b/c.wit");
        assertInstanceOf(ClasspathSource.class, source);
        assertEquals("a/b/c.wit", ((ClasspathSource) source).path());

        assertEquals("/c.wit", loader.normalize("/c.wit"));
        assertEquals("/c", loader.normalize("/c"));
    }

    @Test
    void completeMissingSuffix1() {
        var loader = AdvancePathLoaderDecorator.builder()
                .delegate(delegate)
                .cacheEnabled(false)
                .completeMissingSuffix(".wit")
                .build();

        assertEquals("/c.wit", loader.normalize("/c.wit"));
        assertEquals("/c.wit", loader.normalize("c"));
    }

    @Test
    void root() {
        var loader = AdvancePathLoaderDecorator.builder()
                .delegate(delegate)
                .root("/a/b")
                .build();

        assertEquals("/a/b", loader.root());

        loader = AdvancePathLoaderDecorator.builder()
                .delegate(delegate)
                .root("/")
                .build();

        assertEquals("/", loader.root());

        loader = AdvancePathLoaderDecorator.builder()
                .delegate(delegate)
                .root("/a/b/")
                .build();

        assertEquals("/a/b", loader.root());
    }
}
