// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.operators;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.BinaryOperator;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.core.ast.Optimizable;
import org.febit.wit.core.ast.expressions.DirectValue;
import org.febit.wit.util.ALU;

/**
 *
 * @author zqq90
 */
public final class Or extends BinaryOperator implements Optimizable {

    public Or(Expression leftExpr, Expression rightExpr, int line, int column) {
        super(leftExpr, rightExpr, line, column);
    }

    @Override
    public Object execute(final InternalContext context) {
        Object left = leftExpr.execute(context);
        return ALU.isTrue(left)
                ? left
                : rightExpr.execute(context);
    }

    @Override
    public Expression optimize() {
        return (leftExpr instanceof DirectValue && rightExpr instanceof DirectValue)
                ? new DirectValue(ALU.or(((DirectValue) leftExpr).value, ((DirectValue) rightExpr).value), line, column)
                : this;
    }
}
