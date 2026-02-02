// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang;

import org.febit.wit.InternalContext;
import org.febit.wit.lang.ast.Statement;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface BreakpointListener {

    void onBreakpoint(@Nullable Object label, InternalContext context, Statement statement, @Nullable Object result);
}
