// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.loaders.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.loaders.Loader;
import org.febit.wit.loaders.LoaderDecorator;
import org.febit.wit.runtime.Resource;

import java.util.List;

@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "of")
public class SecurityLoaderDecorator implements LoaderDecorator {

    @Getter
    private final Loader delegate;
    private final List<String> allows;

    @Override
    public Resource get(String path) {
        for (var allow : this.allows) {
            if (path.startsWith(allow)) {
                return this.delegate.get(path);
            }
        }
        return new NotExistResource(path, "Access denied.");
    }

}
