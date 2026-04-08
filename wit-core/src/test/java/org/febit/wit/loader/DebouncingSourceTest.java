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
package org.febit.wit.loader;

import lombok.Setter;
import org.febit.wit.io.Source;
import org.febit.wit.io.loader.DebouncingSource;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.io.Reader;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class DebouncingSourceTest {

    @Setter
    @NullMarked
    public static class InnerSource implements Source {

        private long version;

        @Override
        public boolean exists() {
            return true;
        }

        @Override
        public BeginWith beginWith() {
            return BeginWith.TEMPLATE;
        }

        @Override
        public Reader open() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public long version() {
            return version;
        }
    }

    @Test
    void test() {
        var timeout = 100;
        var inner = new InnerSource();
        var startMs = 1000L;

        var res = spy(new DebouncingSource(inner, timeout));

        when(res.now()).thenReturn(startMs);
        inner.setVersion(100L);
        assertEquals(100L, res.version());
        assertEquals(100L, inner.version());
        assertEquals(100L, res.version());
        assertEquals(100L, inner.version());
        inner.setVersion(1000L);
        assertEquals(1000L, inner.version());
        assertEquals(100L, res.version());

        when(res.now()).thenReturn(startMs + 1L);
        assertEquals(100L, res.version());

        when(res.now()).thenReturn(startMs - 1L);
        assertEquals(100L, res.version());

        when(res.now()).thenReturn(startMs + timeout - 1L);
        assertEquals(100L, res.version());

        when(res.now()).thenReturn(startMs + timeout);
        assertEquals(1000L, res.version());

        when(res.now()).thenReturn(startMs + timeout + 1);
        assertEquals(1000L, res.version());
        inner.setVersion(1001L);
        assertEquals(1001L, inner.version());
        assertEquals(1000L, res.version());

        when(res.now()).thenReturn(startMs + timeout + timeout * 2);
        assertEquals(1001L, res.version());
    }

}
