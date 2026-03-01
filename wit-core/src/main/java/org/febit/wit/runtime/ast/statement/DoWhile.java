// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.statement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.ALU;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.FlowControl;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.WithFlowControl;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class DoWhile implements Statement, WithFlowControl {

    private final Expression condition;
    private final int frame;
    private final Statement[] body;
    private final List<FlowControl> flowControls;
    private final int label;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        context.variables().onFrame(frame, () -> execute0(context));
        return null;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private void execute0(InternalContext context) {
        var statements = this.body;
        var myLabel = this.label;
        var cond = this.condition;
        do {
            if (context.visitLoopFlow(statements, myLabel)) {
                // End this loop if not continue
                break;
            }
        } while (ALU.isTruly(cond.execute(context)));
    }

    @Override
    public void collectFlowControls(Consumer<FlowControl> collector) {
        flowControls.forEach(collector);
    }
}
