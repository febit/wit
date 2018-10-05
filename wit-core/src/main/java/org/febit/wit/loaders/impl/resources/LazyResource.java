// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.loaders.impl.resources;

import org.febit.wit.loaders.Resource;

import java.io.IOException;
import java.io.Reader;

/**
 * @author zqq90
 * @since 1.4.0
 */
public class LazyResource implements Resource {

    public static class VersionStamp {

        public final long version;
        public final long expire;

        public VersionStamp(long version, long expire) {
            this.version = version;
            this.expire = expire;
        }
    }

    protected final long timeout;
    protected final Resource resource;
    protected volatile VersionStamp versionStamp;

    public LazyResource(Resource resource, int timeout) {
        this(resource, (long) timeout);
    }

    public LazyResource(Resource resource, long timeout) {
        this.timeout = timeout;
        this.resource = resource;
    }

    @Override
    public Reader openReader() throws IOException {
        // force clear version stamp
        return this.resource.openReader();
    }

    protected long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    @Override
    public long version() {
        VersionStamp stamp = this.versionStamp;
        if (stamp == null
                || stamp.expire <= currentTimeMillis()) {
            stamp = new VersionStamp(this.resource.version(),
                    currentTimeMillis() + this.timeout);
            this.versionStamp = stamp;
        }
        return stamp.version;
    }

    /**
     * @since 1.4.1
     */
    @Override
    public boolean exists() {
        return this.resource.exists();
    }

    /**
     * @since 2.0.0
     */
    @Override
    public boolean isCodeFirst() {
        return this.resource.isCodeFirst();
    }
}
