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
        final Statement[] stats = this.statements;
        final int preIndex = context.indexer;
        context.indexer = indexer;
        do {
            context.execute(stats);
        } while (ALU.isTrue(whileExpr.execute(context)));
        context.indexer = preIndex;
        return null;
    }
}
