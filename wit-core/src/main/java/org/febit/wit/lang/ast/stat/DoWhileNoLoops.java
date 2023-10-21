// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.Statement;
import org.febit.wit.lang.ALU;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public final class DoWhileNoLoops implements Statement {

    private final Expression whileExpr;
    private final int indexer;
    private final Statement[] statements;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(final InternalContext context) {
        return context.pushIndexer(indexer, this::execute0);
    }

    @Nullable
    @SuppressWarnings("UnnecessaryLocalVariable")
    private Object execute0(final InternalContext context) {
        var stats = this.statements;
        do {
            context.visit(stats);
        } while (ALU.isTrue(whileExpr.execute(context)));
        return null;
    }
}
