// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.statement;

import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.Undefined;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.FlowControl;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.WithFlowControl;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public record Return(
        @Nullable Expression value,
        Position position
) implements Statement, WithFlowControl {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        var result = value != null
                ? value.execute(context)
                : Undefined.UNDEFINED;
        context.flow().toReturn(result);
        return null;
    }

    @Override
    public void collectFlowControls(Consumer<FlowControl> collector) {
        collector.accept(
                new FlowControl(FlowControl.Kind.RETURN, 0, position)
        );
    }
}
