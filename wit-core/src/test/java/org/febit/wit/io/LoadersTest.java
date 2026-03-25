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

import org.febit.wit.io.loader.AdvancePathLoaderDecorator;
import org.febit.wit.io.loader.ClasspathLoader;
import org.febit.wit.io.loader.FileSystemLoader;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;

import static org.junit.jupiter.api.Assertions.*;

class LoadersTest {

    @Test
    void classpathDefaults() {
        var loader = Loaders.classpath()
                .build();

        assertInstanceOf(AdvancePathLoaderDecorator.class, loader);

        var adv = (AdvancePathLoaderDecorator) loader;
        assertFalse(adv.cacheEnabled());
        assertNull(adv.root());
        assertNull(adv.completeMissingSuffix());

        assertInstanceOf(ClasspathLoader.class, adv.delegate());

        var classpathLoader = (ClasspathLoader) adv.delegate();
        assertSame(ClassLoader.getSystemClassLoader(), classpathLoader.classLoader());
        assertSame(StandardCharsets.UTF_8, classpathLoader.charset());
        assertSame(Source.BeginWith.SCRIPT, classpathLoader.beginWith());
    }

    @Test
    void classpath() {
        var loader = Loaders.classpath()
                .classLoader(ClassLoader.getSystemClassLoader())
                .charset(StandardCharsets.US_ASCII)
                .beginWith(Source.BeginWith.TEMPLATE)
                .cacheEnabled(true)
                .root("root")
                .completeMissingSuffix(".wit")
                .build();

        assertInstanceOf(AdvancePathLoaderDecorator.class, loader);
        var adv = (AdvancePathLoaderDecorator) loader;
        assertTrue(adv.cacheEnabled());
        assertEquals("root", adv.root());
        assertEquals(".wit", adv.completeMissingSuffix());

        assertInstanceOf(ClasspathLoader.class, adv.delegate());
        var classpathLoader = (ClasspathLoader) adv.delegate();
        assertSame(ClassLoader.getSystemClassLoader(), classpathLoader.classLoader());
        assertSame(StandardCharsets.US_ASCII, classpathLoader.charset());
        assertSame(Source.BeginWith.TEMPLATE, classpathLoader.beginWith());
    }

    @Test
    void fileSystemDefaults() {
        var loader = Loaders.fileSystem()
                .build();

        assertInstanceOf(AdvancePathLoaderDecorator.class, loader);

        var adv = (AdvancePathLoaderDecorator) loader;
        assertFalse(adv.cacheEnabled());
        assertNull(adv.root());
        assertNull(adv.completeMissingSuffix());

        var delegate = adv.delegate();
        assertInstanceOf(FileSystemLoader.class, delegate);

        var fileSystemLoader = (FileSystemLoader) delegate;
        assertSame(FileSystems.getDefault(), fileSystemLoader.fileSystem());
        assertSame(StandardCharsets.UTF_8, fileSystemLoader.charset());
        assertSame(Source.BeginWith.SCRIPT, fileSystemLoader.beginWith());
    }

    @Test
    void fileSystem() {
        var loader = Loaders.fileSystem()
                .fileSystem(FileSystems.getDefault())
                .charset(StandardCharsets.US_ASCII)
                .beginWith(Source.BeginWith.TEMPLATE)
                .cacheEnabled(true)
                .root("root")
                .completeMissingSuffix(".wit")
                .build();

        assertInstanceOf(AdvancePathLoaderDecorator.class, loader);
        var adv = (AdvancePathLoaderDecorator) loader;
        assertTrue(adv.cacheEnabled());
        assertEquals("root", adv.root());
        assertEquals(".wit", adv.completeMissingSuffix());

        var delegate = adv.delegate();
        assertInstanceOf(FileSystemLoader.class, delegate);
        var fileSystemLoader = (FileSystemLoader) delegate;
        assertSame(FileSystems.getDefault(), fileSystemLoader.fileSystem());
        assertSame(StandardCharsets.US_ASCII, fileSystemLoader.charset());
        assertSame(Source.BeginWith.TEMPLATE, fileSystemLoader.beginWith());
    }
}

