// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.stat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.ALU;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class DoWhileNoLoops implements Statement {

    private final Expression whileExpr;
    private final int indexer;
    private final Statement[] statements;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        context.heap().onFrame(indexer, () -> execute0(context));
        return null;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private void execute0(InternalContext context) {
        var stats = this.statements;
        do {
            context.visit(stats);
        } while (ALU.isTruly(whileExpr.execute(context)));
    }
}
