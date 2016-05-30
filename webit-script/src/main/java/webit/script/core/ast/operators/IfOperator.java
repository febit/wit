// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.operators;

import webit.script.Context;
import webit.script.core.ast.Expression;
import webit.script.util.ALU;

/**
 *
 * @author Zqq
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
    public Object execute(final Context context) {
        return (ALU.isTrue(ifExpr.execute(context)) ? leftValueExpr : rightValueExpr).execute(context);
    }
}
