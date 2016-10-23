// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.operators;

import org.febit.wit.core.ast.Expression;
import org.febit.wit.core.ast.ResetableValueExpression;
import org.febit.wit.core.ast.SelfOperator;
import org.febit.wit.util.ALU;

/**
 *
 * @author zqq90
 */
public final class SelfBitXor extends SelfOperator{

    public SelfBitXor(ResetableValueExpression leftExp, Expression rightExp, int line, int column) {
        super(leftExp, rightExp, line, column);
    }

    @Override
    protected Object doOperate(final Object right, final Object left) {
        return ALU.bitXor(left, right);
    }
}
