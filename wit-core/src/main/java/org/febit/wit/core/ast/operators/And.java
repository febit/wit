// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.operators;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.core.ast.expressions.DirectValue;
import org.febit.wit.util.ALU;
import org.febit.wit.util.StatementUtil;

/**
 * @author zqq90
 */
public final class And extends BiOperator {

    public And(Expression leftExpr, Expression rightExpr, int line, int column) {
        super(leftExpr, rightExpr, line, column);
    }

    @Override
    public Object execute(final InternalContext context) {
        Object left = leftExpr.execute(context);
        return ALU.isTrue(left)
                ? rightExpr.execute(context)
                : left;
    }

    @Override
    public Expression optimize() {
        if (!StatementUtil.isImmutableDirectValue(leftExpr)) {
            return this;
        }
        if (StatementUtil.isImmutableDirectValue(rightExpr)) {
            return new DirectValue(
                    ALU.and(((DirectValue) leftExpr).value, ((DirectValue) rightExpr).value),
                    line, column);
        } else {
            return ALU.not(((DirectValue) leftExpr).value)
                    ? leftExpr : rightExpr;
        }
    }
}
