// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.ResetableValueExpression;
import webit.script.util.ALU;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class MinusMinusAfterOperator extends AbstractExpression {

    private final ResetableValueExpression expr;

    public MinusMinusAfterOperator(ResetableValueExpression expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    public Object execute(final Context context) {
        final Object value;
        final ResetableValueExpression _expr;
        StatmentUtil.executeSetValue(_expr = this.expr, context, ALU.minusOne(
                value = StatmentUtil.execute(_expr, context)));
        return value;
    }
}
