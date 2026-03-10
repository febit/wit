// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.statement;

import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.FlowControl;
import org.febit.wit.runtime.ast.FlowControls;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.WithFlowControl;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public record TryCatchFinally(
        int exceptionVarIndex,
        Statement body,
        Statement catchBody,
        @Nullable Statement finallyBody,
        Position position
) implements Statement, WithFlowControl {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        try {
            body.execute(context);
        } catch (Exception e) {
            context.variables().set(exceptionVarIndex, e);
            catchBody.execute(context);
        } finally {
            if (finallyBody != null) {
                finallyBody.execute(context);
            }
        }
        return null;
    }

    @Override
    public void bubbleFlowControls(Consumer<FlowControl> collector) {
        FlowControls.collect(collector, body, catchBody, finallyBody);
    }
}
