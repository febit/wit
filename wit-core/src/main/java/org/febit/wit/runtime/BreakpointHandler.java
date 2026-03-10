// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime;

import org.febit.wit.runtime.ast.Statement;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface BreakpointHandler {

    void handle(@Nullable Object mark, InternalContext context, Statement statement, @Nullable Object result);
}
