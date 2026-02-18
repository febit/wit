package org.febit.wit.loader;

import lombok.Setter;
import org.febit.wit.loader.impl.DebouncedSource;
import org.febit.wit.runtime.Source;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.io.Reader;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class DebouncedSourceTest {

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
        public Reader openReader() {
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

        var res = spy(new DebouncedSource(inner, timeout));

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
