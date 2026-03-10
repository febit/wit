// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.statement;

import org.febit.wit.runtime.ALU;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.jspecify.annotations.Nullable;

public record WhileNonFlow(
        int frame,
        Expression condition,
        StatementBatch body,
        Position position
) implements Statement {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        context.variables().onFrame(frame, () -> execute0(context));
        return null;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private void execute0(InternalContext context) {
        var batch = this.body;
        var cond = this.condition;
        while (ALU.isTruly(cond.execute(context))) {
            batch.execute(context);
        }
    }
}
