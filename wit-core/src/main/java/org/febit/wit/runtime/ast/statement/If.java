// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.statement;

import org.febit.wit.runtime.ALU;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AstUtils;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.FlowControl;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.WithFlowControl;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public record If(
        Expression condition,
        Statement then,
        Position position
) implements Statement, WithFlowControl {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        if (ALU.isTruly(condition.execute(context))) {
            return then.execute(context);
        }
        return null;
    }

    @Override
    public void collectFlowControls(Consumer<FlowControl> collector) {
        AstUtils.collectFlowControls(collector, then);
    }
}
