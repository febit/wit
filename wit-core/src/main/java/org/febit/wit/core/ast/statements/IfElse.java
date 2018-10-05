// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.statements;

import org.febit.wit.InternalContext;
import org.febit.wit.core.LoopInfo;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.core.ast.Loopable;
import org.febit.wit.core.ast.Statement;
import org.febit.wit.util.ALU;
import org.febit.wit.util.StatementUtil;

import java.util.List;

/**
 * @author zqq90
 */
public final class IfElse extends Statement implements Loopable {

    private final Expression ifExpr;
    private final Statement thenStatement;
    private final Statement elseStatement;

    public IfElse(Expression ifExpr, Statement thenStatement, Statement elseStatement, int line, int column) {
        super(line, column);
        this.ifExpr = ifExpr;
        this.thenStatement = thenStatement;
        this.elseStatement = elseStatement;
    }

    @Override
    public Object execute(final InternalContext context) {
        return (ALU.isTrue(ifExpr.execute(context))
                ? thenStatement : elseStatement).execute(context);
    }

    @Override
    public List<LoopInfo> collectPossibleLoops() {
        return StatementUtil.collectPossibleLoops(thenStatement, elseStatement);
    }
}
