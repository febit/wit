package org.febit.wit.loaders;

import org.febit.wit.lang.Resource;
import org.jspecify.annotations.Nullable;

public interface LoaderDecorator extends Loader {

    Loader delegate();

    @Override
    default Resource get(String path) {
        return delegate().get(path);
    }

    @Nullable
    @Override
    default String sibling(@Nullable String refer, String path) {
        return delegate().sibling(refer, path);
    }

    @Nullable
    @Override
    default String normalize(@Nullable String path) {
        return delegate().normalize(path);
    }

    @Override
    default boolean isCacheEnabled(String path) {
        return delegate().isCacheEnabled(path);
    }
}
