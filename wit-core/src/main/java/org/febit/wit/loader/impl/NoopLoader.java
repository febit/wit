package org.febit.wit.loader.impl;

import org.febit.wit.loader.Loader;
import org.febit.wit.runtime.Source;
import org.jspecify.annotations.Nullable;

/**
 * A loader that without any sources.
 */
public class NoopLoader implements Loader {

    @Override
    public Source get(String path) {
        return new NoSuchSource(path, "No such source: " + path);
    }

    @Override
    public @Nullable String sibling(@Nullable String refer, String relative) {
        return relative;
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
