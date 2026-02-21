// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.stat;

import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.IBlock;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.jspecify.annotations.Nullable;

public record BlockWithoutLoops(
        int frame,
        Statement[] statements,
        Position position
) implements IBlock {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        context.variables().onFrame(frame,
                () -> context.visit(statements)
        );
        return null;
    }

    @Override
    public boolean hasLoopFlags() {
        return false;
    }

    @Override
    public Statement optimize() {
        return statements.length == 0 ? NoopStatement.INSTANCE : this;
    }
}
