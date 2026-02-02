package org.febit.wit.loader;

import org.febit.wit.runtime.Source;
import org.jspecify.annotations.Nullable;

public interface LoaderDecorator extends Loader {

    Loader delegate();

    @Override
    default Source get(String path) {
        return delegate().get(path);
    }

    @Nullable
    @Override
    default String sibling(@Nullable String refer, String relative) {
        return delegate().sibling(refer, relative);
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
