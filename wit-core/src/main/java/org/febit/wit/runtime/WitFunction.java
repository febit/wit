// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime;

import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface WitFunction {

    @Nullable
    Object apply(InternalContext context, @Nullable Object @Nullable [] args);

    @FunctionalInterface
    interface Constable extends WitFunction {

        @Nullable
        default Object apply(InternalContext context, @Nullable Object @Nullable [] args) {
            return apply(args);
        }

        @Nullable
        Object apply(@Nullable Object @Nullable [] args);
    }
}
