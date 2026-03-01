// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.statement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.FlowControl;
import org.febit.wit.runtime.ast.IBlock;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class Block implements IBlock {

    @Getter
    private final int frame;
    @Getter
    private final Statement[] statements;
    private final List<FlowControl> flowControls;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        context.variables().onFrame(frame,
                () -> context.visit(statements)
        );
        return null;
    }

    @Override
    public void collectFlowControls(Consumer<FlowControl> collector) {
        flowControls.forEach(collector);
    }

    @Override
    public boolean needFlowControlCheck() {
        return true;
    }
}
