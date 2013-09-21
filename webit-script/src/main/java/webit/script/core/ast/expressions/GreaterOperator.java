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
public final class GreaterOperator extends BinaryOperator implements Optimizable {

    public GreaterOperator(Expression leftExpr, Expression rightExpr, int line, int column) {
        super(leftExpr, rightExpr, line, column);
    }

    public Object execute(final Context context) {
        return ALU.greater(StatmentUtil.execute(leftExpr, context), StatmentUtil.execute(rightExpr, context));
    }

    public Expression optimize() {
        return (leftExpr instanceof DirectValue && rightExpr instanceof DirectValue)
                ? new DirectValue(ALU.greater(((DirectValue) leftExpr).value, ((DirectValue) rightExpr).value), line, column)
                : this;
    }
}
