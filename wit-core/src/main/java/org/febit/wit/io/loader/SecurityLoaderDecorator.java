// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.io.loader;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.io.Loader;
import org.febit.wit.io.Source;

import java.util.List;

@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "of")
public class SecurityLoaderDecorator implements Loader.Decorator {

    @Getter
    private final Loader delegate;
    private final List<String> allows;

    @Override
    public Source get(String path) {
        for (var allow : this.allows) {
            if (path.startsWith(allow)) {
                return this.delegate.get(path);
            }
        }
        return new EmptySource(path, "Access denied.");
    }

}
