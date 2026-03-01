// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.statement;

import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.FlowControl;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.WithFlowControl;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public record Continue(
        int label,
        Position position
) implements Statement, WithFlowControl {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        context.flow().toContinue(label);
        return null;
    }

    @Override
    public void collectFlowControls(Consumer<FlowControl> collector) {
        collector.accept(
                new FlowControl(FlowControl.Kind.CONTINUE, label, position)
        );
    }
}
