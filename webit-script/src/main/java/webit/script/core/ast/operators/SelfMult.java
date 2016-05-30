// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.operators;

import webit.script.core.ast.Expression;
import webit.script.core.ast.ResetableValueExpression;
import webit.script.core.ast.SelfOperator;
import webit.script.util.ALU;

/**
 *
 * @author zqq90
 */
public final class SelfMult extends SelfOperator{

    public SelfMult(ResetableValueExpression leftExp, Expression rightExp, int line, int column) {
        super(leftExp, rightExp, line, column);
    }

    @Override
    protected Object doOperate(final Object right, final Object left) {
        return ALU.mult(left, right);
    }
}
