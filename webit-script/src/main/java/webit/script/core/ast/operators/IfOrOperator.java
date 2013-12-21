// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.operators;

import webit.script.Context;
import webit.script.core.ast.BinaryOperator;
import webit.script.core.ast.Expression;
import webit.script.util.ALU;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public final class IfOrOperator extends BinaryOperator {

    public IfOrOperator(Expression leftExp, Expression rightExp, int line, int column) {
        super(leftExp, rightExp, line, column);
    }

    public Object execute(final Context context) {
        Object ifResult;
        return ALU.isTrue(ifResult = StatementUtil.execute(leftExpr, context))
                ? ifResult
                : StatementUtil.execute(rightExpr, context);
    }
}