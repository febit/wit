// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.debug;

import jakarta.annotation.Nullable;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.ast.Statement;

/**
 * @author zqq90
 */
@FunctionalInterface
public interface BreakpointListener {

    void onBreakpoint(@Nullable Object label, InternalContext context, Statement statement, @Nullable Object result);
}
