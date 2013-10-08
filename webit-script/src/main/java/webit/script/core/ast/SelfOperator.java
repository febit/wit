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

    public Object execute(final Context context) {
        //final ResetableValue resetableValue;
        ResetableValueExpression _leftExpr;
        return StatmentUtil.executeSetValue(_leftExpr = this.leftExpr, context, doOperate(StatmentUtil.execute(rightExpr, context),
                StatmentUtil.execute(_leftExpr, context)));
    }

    protected abstract Object doOperate(Object right, Object left);
}
