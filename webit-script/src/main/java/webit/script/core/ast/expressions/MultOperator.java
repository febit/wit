// Copyright (c) 2013, Webit Team. All Rights Reserved.

package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.BinaryOperator;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Optimizable;
import webit.script.util.ALU;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class MultOperator extends BinaryOperator implements Optimizable {

    public MultOperator(Expression leftExpr, Expression rightExpr, int line, int column) {
        super(leftExpr, rightExpr, line, column);
    }

    @Override
    public Object execute(Context context, boolean needReturn) {
        return ALU.mult(StatmentUtil.execute(leftExpr, context), StatmentUtil.execute(rightExpr, context));
    }

    @Override
    public Expression optimize() {
        if (leftExpr instanceof DirectValue && rightExpr instanceof DirectValue) {
            return new DirectValue(ALU.mult(((DirectValue) leftExpr).value, ((DirectValue) rightExpr).value), line, column);
        }
        return this;
    }
}
