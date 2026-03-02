package org.febit.wit.io.loader;

import org.febit.wit.io.Loader;
import org.febit.wit.util.PathUtils;
import org.jspecify.annotations.Nullable;

public interface PathBasedLoader extends Loader {

    @Nullable
    @Override
    default String sibling(@Nullable String refer, String relative) {
        return PathUtils.sibling(refer, relative);
    }

    @Nullable
    @Override
    default String normalize(@Nullable String path) {
        return PathUtils.normalize(path);
    }

    @Override
    default boolean isCacheEnabled(String path) {
        return false;
    }
}
