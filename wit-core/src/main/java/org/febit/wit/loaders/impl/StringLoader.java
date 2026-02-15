// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.loaders.impl;

import org.febit.wit.loaders.Loader;
import org.febit.wit.runtime.Source;
import org.jspecify.annotations.Nullable;

@lombok.Builder(
        builderClassName = "Builder"
)
public class StringLoader implements Loader {

    private final boolean cacheEnabled;
    private final boolean codeFirst;

    @Override
    public Source get(String path) {
        return new StringSource(path, codeFirst);
    }

    @Override
    public String sibling(@Nullable String refer, String path) {
        return path;
    }

    @Nullable
    @Override
    public String normalize(@Nullable String path) {
        return path;
    }

    @Override
    public boolean isCacheEnabled(String path) {
        return cacheEnabled;
    }
}
