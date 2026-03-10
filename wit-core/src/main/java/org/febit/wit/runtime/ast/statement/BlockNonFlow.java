// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.statement;

import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.FlowControl;
import org.febit.wit.runtime.ast.IBlock;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public record BlockNonFlow(
        int scope,
        StatementBatch bodyBatch,
        Position position
) implements IBlock {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        context.variables().onScope(scope,
                () -> bodyBatch.execute(context)
        );
        return null;
    }

    @Override
    public List<StatementBatch> body() {
        return List.of(bodyBatch);
    }

    @Override
    public void bubbleFlowControls(Consumer<FlowControl> collector) {
        // No flow control.
    }

    @Override
    public Statement optimize() {
        return bodyBatch.isEmpty() ? NoopStatement.INSTANCE : this;
    }
}
