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
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DispatchLoaderTest {

    final Loader loaderA = mock(Loader.class);
    final Loader loaderB = mock(Loader.class);
    final Loader loaderAbc = mock(Loader.class);
    final Loader loaderFallback = mock(Loader.class);

    final List<Loader> all = List.of(
            loaderA, loaderB, loaderAbc, loaderFallback
    );

    final Loader loader = Loaders.dispatch()
            .rule("a:b:c:", loaderAbc)
            .rule("abc:", loaderAbc)
            .rule("a:", loaderA)
            .rule("b:", loaderB)
            .fallback(loaderFallback)
            .build();

    private void resetAll() {
        all.forEach(Mockito::reset);
        all.forEach(loader -> {
            when(loader.get(any()))
                    .thenReturn(new EmptySource("x", "mocked"));
            when(loader.normalize(any()))
                    .thenReturn("normalized");

            when(loader.isCacheEnabled(any()))
                    .thenReturn(true);
            when(loader.sibling(any(), any()))
                    .thenAnswer(inv -> {
                        String refer = inv.getArgument(0);
                        String path = inv.getArgument(1);
                        return refer + ">" + path;
                    });
        });
    }

    @Test
    void get() {
        resetAll();
        loader.get("a:b:c:x.wit");
        verify(loaderAbc).get("x.wit");
        verify(loaderA, never()).get(anyString());
        verify(loaderB, never()).get(anyString());
        verify(loaderFallback, never()).get(anyString());

        resetAll();
        loader.get("abc:a:x.wit");
        verify(loaderAbc).get("a:x.wit");
        verify(loaderA, never()).get(anyString());
        verify(loaderB, never()).get(anyString());
        verify(loaderFallback, never()).get(anyString());

        resetAll();
        loader.get("a: b:c:x.wit");
        verify(loaderA).get(" b:c:x.wit");
        verify(loaderAbc, never()).get(anyString());
        verify(loaderB, never()).get(anyString());
        verify(loaderFallback, never()).get(anyString());

        resetAll();
        loader.get("b: x.wit");
        verify(loaderB).get(" x.wit");
        verify(loaderAbc, never()).get(anyString());
        verify(loaderA, never()).get(anyString());
        verify(loaderFallback, never()).get(anyString());

        resetAll();
        loader.get("x: x.wit");
        verify(loaderFallback).get("x: x.wit");
        verify(loaderAbc, never()).get(anyString());
        verify(loaderA, never()).get(anyString());
        verify(loaderB, never()).get(anyString());

        resetAll();
        loader.get("abc.wit");
        verify(loaderFallback).get("abc.wit");
        verify(loaderAbc, never()).get(anyString());
        verify(loaderA, never()).get(anyString());
        verify(loaderB, never()).get(anyString());

        resetAll();
        loader.get(null);
        verify(loaderFallback).get(null);
        verify(loaderAbc, never()).get(anyString());
        verify(loaderA, never()).get(anyString());
        verify(loaderB, never()).get(anyString());
    }

    @Test
    void isCacheEnabled() {
        resetAll();
        assertTrue(loader.isCacheEnabled("a:b:c:x.wit"));
        verify(loaderAbc).isCacheEnabled("x.wit");
        verify(loaderA, never()).isCacheEnabled(anyString());
        verify(loaderB, never()).isCacheEnabled(anyString());
        verify(loaderFallback, never()).isCacheEnabled(anyString());

        resetAll();
        assertTrue(loader.isCacheEnabled("abc:a:x.wit"));
        verify(loaderAbc).isCacheEnabled("a:x.wit");
        verify(loaderA, never()).isCacheEnabled(anyString());
        verify(loaderB, never()).isCacheEnabled(anyString());
        verify(loaderFallback, never()).isCacheEnabled(anyString());

        resetAll();
        assertTrue(loader.isCacheEnabled("abc.wit"));
        verify(loaderFallback).isCacheEnabled("abc.wit");

        resetAll();
        assertTrue(loader.isCacheEnabled(null));
        verify(loaderFallback).isCacheEnabled(null);
    }

    @Test
    void sibling() {
        resetAll();
        assertEquals(
                "a:b:c:normalized",
                loader.sibling("refer", "a:b:c:x.wit")
        );
        verify(loaderAbc).normalize("x.wit");
        verify(loaderAbc, never()).sibling(anyString(), anyString());
        verify(loaderA, never()).normalize(anyString());
        verify(loaderA, never()).sibling(anyString(), anyString());

        resetAll();
        assertEquals(
                "a:b:c:normalized",
                loader.sibling("a:x.wit", "a:b:c:x.wit")
        );
        verify(loaderAbc).normalize("x.wit");
        verify(loaderA, never()).normalize(anyString());

        resetAll();
        assertEquals(
                "a:refer>x.wit",
                loader.sibling("a:refer", "x.wit")
        );
        verify(loaderA).sibling("refer", "x.wit");

        resetAll();
        assertEquals(
                "refer>x: x.wit",
                loader.sibling("refer", "x: x.wit")
        );
        verify(loaderFallback).sibling("refer", "x: x.wit");

        resetAll();
        assertEquals(
                "null>x.wit",
                loader.sibling(null, "x.wit")
        );
        verify(loaderFallback).sibling(null, "x.wit");
    }

    @Test
    void normalize() {
        resetAll();
        assertEquals(
                "a:b:c:normalized",
                loader.normalize("a:b:c:x.wit")
        );
        verify(loaderAbc).normalize("x.wit");
        verify(loaderA, never()).normalize(anyString());
        verify(loaderB, never()).normalize(anyString());
        verify(loaderFallback, never()).normalize(anyString());

        resetAll();
        assertEquals(
                "abc:normalized",
                loader.normalize("abc:a:x.wit")
        );
        verify(loaderAbc).normalize("a:x.wit");
        verify(loaderA, never()).normalize(anyString());
        verify(loaderB, never()).normalize(anyString());
        verify(loaderFallback, never()).normalize(anyString());

        resetAll();
        assertEquals(
                "a:normalized",
                loader.normalize("a: b:c:x.wit")
        );
        verify(loaderA).normalize(" b:c:x.wit");

        resetAll();
        assertEquals(
                "b:normalized",
                loader.normalize("b: x.wit")
        );
        verify(loaderB).normalize(" x.wit");

        resetAll();
        assertEquals(
                "normalized",
                loader.normalize("x: x.wit")
        );
        verify(loaderFallback).normalize("x: x.wit");

        resetAll();
        assertEquals(
                "normalized",
                loader.normalize("abc.wit")
        );
        verify(loaderFallback).normalize("abc.wit");

        resetAll();
        assertEquals(
                "normalized",
                loader.normalize(null)
        );
        verify(loaderFallback).normalize(null);
        verify(loaderFallback, never()).normalize(anyString());
        verify(loaderA, never()).normalize(anyString());
        verify(loaderB, never()).normalize(anyString());
        verify(loaderAbc, never()).normalize(anyString());
    }
}
