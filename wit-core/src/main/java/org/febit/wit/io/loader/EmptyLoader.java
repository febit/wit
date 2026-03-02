package org.febit.wit.io.loader;

import org.febit.wit.io.Loader;
import org.febit.wit.io.Source;
import org.jspecify.annotations.Nullable;

/**
 * A loader that without any sources.
 */
public class EmptyLoader implements Loader {

    @Override
    public Source get(String path) {
        return new EmptySource(path, "No such source: " + path);
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
