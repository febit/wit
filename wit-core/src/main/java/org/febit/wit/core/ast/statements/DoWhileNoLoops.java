// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.statements;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.core.ast.Statement;
import org.febit.wit.util.ALU;
import org.febit.wit.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public final class DoWhileNoLoops extends Statement {

    private final Expression whileExpr;
    private final int indexer;
    private final Statement[] statements;

    public DoWhileNoLoops(Expression whileExpr, int indexer, Statement[] statements, int line, int column) {
        super(line, column);
        this.whileExpr = whileExpr;
        this.indexer = indexer;
        this.statements = statements;
    }

    @Override
    public Object execute(final InternalContext context) {
        final Statement[] stats = this.statements;
        final int preIndex = context.indexer;
        context.indexer = indexer;
        do {
            StatementUtil.execute(stats, context);
        } while (ALU.isTrue(whileExpr.execute(context)));
        context.indexer = preIndex;
        return null;
    }
}
