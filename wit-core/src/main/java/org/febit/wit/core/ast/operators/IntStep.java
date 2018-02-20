// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.operators;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.util.ALU;
import org.febit.wit.util.CollectionUtil;

/**
 *
 * @author zqq90
 */
public class IntStep extends BiOperator {

    public IntStep(Expression leftExp, Expression rightExp, int line, int column) {
        super(leftExp, rightExp, line, column);
    }

    @Override
    public Object execute(final InternalContext context) {
        final int left = ALU.requireNumber(leftExpr.execute(context)).intValue();
        final int right = ALU.requireNumber(rightExpr.execute(context)).intValue();
        if (left < right) {
            return CollectionUtil.createIntAscIter(left, right);
        } else {
            return CollectionUtil.createIntDescIter(left, right);
        }
    }
}
