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
package org.febit.wit.extern.servlet;

import jakarta.servlet.ServletContext;
import org.apache.commons.io.IOUtils;
import org.febit.wit.exception.NoSuchSourceException;
import org.febit.wit.io.loader.FileSystemSource;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ServletContextLoaderTest {

    @Test
    void usesFileSystemSourceWhenRealPathExists() {
        var context = mock(ServletContext.class);

        var redirect = "/x.wit";
        when(context.getRealPath(anyString())).thenReturn(redirect);

        var loader = ServletContextLoader.builder()
                .context(context)
                .build();
        var source = loader.get("/tmpl/demo.wit");

        assertInstanceOf(FileSystemSource.class, source);
        assertEquals(Path.of(redirect), ((FileSystemSource) source).path());
    }

    @Test
    void fallsBackToServletContextSourceWhenRealPathMissing() throws Exception {
        var context = mock(ServletContext.class);
        when(context.getRealPath(anyString())).thenReturn(null);

        var payload = "Hello Servlet";
        when(context.getResourceAsStream(anyString()))
                .thenReturn(new ByteArrayInputStream(payload.getBytes(StandardCharsets.UTF_8)));
        when(context.getResource(anyString()))
                .thenReturn(URI.create("file:///fake/some.wit").toURL());

        var loader = ServletContextLoader.builder()
                .context(context)
                .build();
        var source = loader.get("/demo.wit");

        assertInstanceOf(ServletContextSource.class, source);
        assertTrue(source.exists());
        assertEquals(0L, source.version());

        try (var reader = source.open()) {
            assertEquals(payload, IOUtils.toString(reader));
        }
    }

    @Test
    void servletContextSourceNotExists() throws MalformedURLException {
        var context = mock(ServletContext.class);
        when(context.getRealPath(anyString())).thenReturn(null);
        when(context.getResourceAsStream(anyString())).thenReturn(null);
        when(context.getResource(anyString())).thenReturn(null);

        var loader = ServletContextLoader.builder()
                .context(context)
                .build();
        var source = loader.get("/not-exists.wit");

        assertInstanceOf(ServletContextSource.class, source);
        assertFalse(source.exists());
        assertThrows(NoSuchSourceException.class, source::open);

        when(context.getResource(anyString())).thenThrow(new RuntimeException("Resource not found"));
        assertFalse(source.exists());
    }
}
