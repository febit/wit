// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.statement;

import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.FlowControl;
import org.febit.wit.runtime.ast.IBlock;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public record BlockNonFlow(
        int frame,
        Statement[] statements,
        Position position
) implements IBlock {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        context.variables().onFrame(frame,
                () -> context.visitNonFlow(statements)
        );
        return null;
    }

    @Override
    public boolean needFlowControlCheck() {
        return false;
    }

    @Override
    public void collectFlowControls(Consumer<FlowControl> collector) {
    }

    @Override
    public Statement optimize() {
        return statements.length == 0 ? NoopStatement.INSTANCE : this;
    }
}
