package org.febit.wit.loaders.impl;

import org.febit.wit.loaders.Loader;
import org.febit.wit.runtime.Resource;
import org.jspecify.annotations.Nullable;

/**
 * A loader that without any resources.
 */
public class NoopLoader implements Loader {

    @Override
    public Resource get(String path) {
        return new NotExistResource(
                path,
                "Resource not found: " + path
        );
    }

    @Override
    public @Nullable String sibling(@Nullable String refer, String path) {
        return path;
    }

    @Override
    public @Nullable String normalize(@Nullable String path) {
        return path;
    }

    @Override
    public boolean isCacheEnabled(String path) {
        return false;
    }
}
