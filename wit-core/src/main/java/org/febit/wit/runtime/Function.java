// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime;

import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface Function {

    @Nullable
    Object apply(InternalContext context, @Nullable Object @Nullable [] args);

    @FunctionalInterface
    interface Constable extends Function {

        @Nullable
        default Object apply(InternalContext context, @Nullable Object @Nullable [] args) {
            return apply(args);
        }

        @Nullable
        Object apply(@Nullable Object @Nullable [] args);
    }
}
