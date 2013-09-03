// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.BinaryOperator;
import webit.script.core.ast.Expression;
import webit.script.util.ALU;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class IfOrOperator extends BinaryOperator {

    public IfOrOperator(Expression leftExp, Expression rightExp, int line, int column) {
        super(leftExp, rightExp, line, column);
    }

    @Override
    public Object execute(Context context, boolean needReturn) {
        Object ifResult = StatmentUtil.execute(leftExpr, context);
        if (ALU.toBoolean(ifResult)) {
            return ifResult;
        } else {
            return StatmentUtil.execute(rightExpr, context);
        }
    }
}
