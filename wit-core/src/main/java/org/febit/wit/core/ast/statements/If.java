// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.statements;

import java.util.List;
import org.febit.wit.InternalContext;
import org.febit.wit.core.LoopInfo;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.core.ast.Loopable;
import org.febit.wit.core.ast.Statement;
import org.febit.wit.util.ALU;
import org.febit.wit.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public final class If extends Statement implements Loopable {

    private final Expression ifExpr;
    private final Statement thenStatement;

    public If(Expression ifExpr, Statement thenStatement, int line, int column) {
        super(line, column);
        this.ifExpr = ifExpr;
        this.thenStatement = thenStatement;
    }

    @Override
    public Object execute(final InternalContext context) {
        if (ALU.isTrue(ifExpr.execute(context))) {
            return thenStatement.execute(context);
        }
        return null;
    }

    @Override
    public List<LoopInfo> collectPossibleLoops() {
        return StatementUtil.collectPossibleLoops(thenStatement);
    }
}
