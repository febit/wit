// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.io.loader;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.io.Loader;
import org.febit.wit.io.Source;

@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "of")
public class DebouncedLoaderDecorator implements LoaderDecorator {

    @Getter
    private final Loader delegate;
    private final long delayMillis;

    @Override
    public Source get(String path) {
        var inner = this.delegate.get(path);
        return this.delayMillis > 0L
                ? new DebouncedSource(inner, this.delayMillis)
                : inner;
    }
}
