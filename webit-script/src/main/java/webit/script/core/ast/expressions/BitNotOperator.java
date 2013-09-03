// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Optimizable;
import webit.script.util.ALU;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class BitNotOperator extends AbstractExpression implements Optimizable {
    private final Expression expr;

    public BitNotOperator(Expression expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    @Override
    public Object execute(Context context, boolean needReturn) {
        return ALU.bitNot(StatmentUtil.execute(expr, context));
    }

    @Override
    public Expression optimize() {
        if (expr instanceof DirectValue) {
            return new DirectValue(ALU.bitNot(((DirectValue) expr).value), line, column);
        }
        return this;
    }
}
