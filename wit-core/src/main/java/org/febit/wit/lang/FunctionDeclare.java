// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang;

import org.febit.wit.InternalContext;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface FunctionDeclare {

    @Nullable
    Object invoke(InternalContext context, @Nullable Object @Nullable [] args);
}
