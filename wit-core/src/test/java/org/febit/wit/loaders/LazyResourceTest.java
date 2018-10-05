package org.febit.wit.loaders;

import org.febit.wit.loaders.impl.resources.LazyResource;
import org.junit.Test;

import java.io.Reader;

import static org.junit.Assert.assertEquals;

/**
 * @author zqq
 */
public class LazyResourceTest {

    public static class InnerResource implements Resource {

        private long version;

        @Override
        public boolean exists() {
            return true;
        }

        @Override
        public boolean isCodeFirst() {
            return false;
        }

        @Override
        public Reader openReader() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public long version() {
            return version;
        }

        public void setVersion(long version) {
            this.version = version;
        }
    }

    @Test
    public void test() {
        final int timeout = 100;
        final InnerResource inner = new InnerResource();

        class LazyResourceFake extends LazyResource {

            private long currentTimeMillis;

            public LazyResourceFake(Resource resource, int timeout) {
                super(resource, timeout);
            }

            @Override
            protected long currentTimeMillis() {
                return currentTimeMillis;
            }

            public void setCurrentTimeMillis(long currentTimeMillis) {
                this.currentTimeMillis = currentTimeMillis;
            }
        }

        final LazyResourceFake lazyResource = new LazyResourceFake(inner, timeout);

        final long startMs = 1000L;
        lazyResource.setCurrentTimeMillis(startMs);
        inner.setVersion(100L);
        assertEquals(lazyResource.version(), 100L);
        assertEquals(inner.version(), 100L);
        assertEquals(lazyResource.version(), 100L);
        assertEquals(inner.version(), 100L);

        inner.setVersion(1000L);
        assertEquals(inner.version(), 1000L);

        assertEquals(lazyResource.version(), 100L);
        lazyResource.setCurrentTimeMillis(startMs + 1L);
        assertEquals(lazyResource.version(), 100L);
        lazyResource.setCurrentTimeMillis(startMs - 1L);
        assertEquals(lazyResource.version(), 100L);
        lazyResource.setCurrentTimeMillis(startMs + timeout - 1L);
        assertEquals(lazyResource.version(), 100L);

        lazyResource.setCurrentTimeMillis(startMs + timeout);
        assertEquals(lazyResource.version(), 1000L);
        lazyResource.setCurrentTimeMillis(startMs + timeout + 1);
        assertEquals(lazyResource.version(), 1000L);

        inner.setVersion(1001L);
        assertEquals(inner.version(), 1001L);
        assertEquals(lazyResource.version(), 1000L);

        lazyResource.setCurrentTimeMillis(startMs + timeout + timeout * 2);
        assertEquals(lazyResource.version(), 1001L);

    }

}
