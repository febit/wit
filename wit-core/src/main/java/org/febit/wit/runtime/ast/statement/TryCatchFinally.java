// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.statement;

import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AstUtils;
import org.febit.wit.runtime.ast.FlowControl;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.WithFlowControl;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public record TryCatchFinally(
        Statement body,
        Statement catchBody,
        @Nullable Statement finallyBody,
        int exceptionVarIndex,
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
    public void collectFlowControls(Consumer<FlowControl> collector) {
        AstUtils.collectFlowControls(collector, body, catchBody, finallyBody);
    }
}
