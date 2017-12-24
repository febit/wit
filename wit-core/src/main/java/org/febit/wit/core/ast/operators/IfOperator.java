// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.operators;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.util.ALU;

/**
 *
 * @author zqq90
 */
public final class IfOperator extends Expression {

    private final Expression ifExpr;
    private final Expression leftValueExpr;
    private final Expression rightValueExpr;

    public IfOperator(Expression ifExpr, Expression leftValueExpr, Expression rightValueExpr, int line, int column) {
        super(line, column);
        this.ifExpr = ifExpr;
        this.leftValueExpr = leftValueExpr;
        this.rightValueExpr = rightValueExpr;
    }

    @Override
    public Object execute(final InternalContext context) {
        return (ALU.isTrue(ifExpr.execute(context)) ? leftValueExpr : rightValueExpr).execute(context);
    }
}
