// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.statement;

import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.FlowControl;
import org.febit.wit.runtime.ast.IBlock;
import org.febit.wit.runtime.ast.Position;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public record Block(
        int scope,
        List<StatementBatch> body,
        List<FlowControl> bubbledFlowControls,
        Position position
) implements IBlock {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        context.variables().onScope(scope,
                () -> context.visitBatches(body)
        );
        return null;
    }

    @Override
    public void bubbleFlowControls(Consumer<FlowControl> collector) {
        bubbledFlowControls.forEach(collector);
    }
}
