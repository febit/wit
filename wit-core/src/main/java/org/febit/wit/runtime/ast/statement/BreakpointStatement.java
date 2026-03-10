// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.statement;

import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.jspecify.annotations.Nullable;

public record BreakpointStatement(
        @Nullable Object mark,
        @Nullable Statement supervised,
        Position position
) implements Statement {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        if (supervised != null) {
            supervised.execute(context);
        }
        var handler = context.breakpointHandler();
        if (handler != null) {
            handler.handle(mark, context, this, null);
        }
        return null;
    }
}
