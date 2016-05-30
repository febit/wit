// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.loaders.impl.resources;

import java.io.IOException;
import java.io.Reader;
import webit.script.loaders.Resource;

/**
 *
 * @since 1.4.0
 * @author zqq90
 */
public class LazyResource implements Resource {

    protected final int timeout;
    protected final Resource resource;
    private long expire;

    public LazyResource(Resource resource, int timeout) {
        this.timeout = timeout;
        this.resource = resource;
        this.expire = 0;
    }

    @Override
    public boolean isModified() {
        if (this.expire >= System.currentTimeMillis()) {
            return false;
        } else {
            recalculateExpire();
            return resource.isModified();
        }
    }

    @Override
    public Reader openReader() throws IOException {
        recalculateExpire();
        return this.resource.openReader();
    }

    private void recalculateExpire() {
        this.expire = System.currentTimeMillis() + this.timeout;
    }

    /**
     * @since 1.4.1
     */
    @Override
    public boolean exists() {
        return this.resource.exists();
    }
}
