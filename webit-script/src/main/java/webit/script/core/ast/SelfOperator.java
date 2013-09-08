// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast;

import webit.script.Context;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public abstract class SelfOperator extends AbstractExpression {

    protected final ResetableValueExpression leftExpr;
    protected final Expression rightExpr;

    public SelfOperator(ResetableValueExpression leftExp, Expression rightExp, int line, int column) {
        super(line, column);
        this.leftExpr = leftExp;
        this.rightExpr = rightExp;
    }

    public Object execute(final Context context, boolean needReturn) {
        final Object rightResult = StatmentUtil.execute(rightExpr, context);
        final ResetableValue resetableValue = StatmentUtil.getResetableValue(leftExpr, context);
        final Object result = doOperate(resetableValue.get(), rightResult);
        resetableValue.set(result);
        return result;
    }

    protected abstract Object doOperate(Object left, Object right);
}
