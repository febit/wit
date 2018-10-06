// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.debug;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.Statement;

/**
 * @author zqq90
 * @deprecated use {@code BreakpointListener} instead.
 */
@Deprecated
@FunctionalInterface
public interface BreakPointListener extends BreakpointListener {

    void onBreak(Object label, InternalContext context, Statement statement, Object result);

    @Override
    default void onBreakpoint(Object label, InternalContext context, Statement statement, Object result) {
        onBreak(label, context, statement, result);
    }
}
