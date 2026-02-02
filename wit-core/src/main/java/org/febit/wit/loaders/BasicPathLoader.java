package org.febit.wit.loaders;

import org.febit.wit.util.PathUtils;
import org.jspecify.annotations.Nullable;

public interface BasicPathLoader extends Loader {

    @Nullable
    @Override
    default String sibling(@Nullable String refer, String path) {
        return PathUtils.sibling(refer, path);
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
