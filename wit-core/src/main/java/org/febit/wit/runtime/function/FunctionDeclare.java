// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.function;

import org.febit.wit.runtime.InternalContext;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface FunctionDeclare {

    @Nullable
    Object apply(InternalContext context, @Nullable Object @Nullable [] args);
}
