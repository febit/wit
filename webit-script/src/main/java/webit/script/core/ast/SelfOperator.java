// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast;

import webit.script.Context;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public abstract class SelfOperator extends Expression {

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
        return StatementUtil.executeSetValue(_leftExpr = this.leftExpr, context, doOperate(StatementUtil.execute(rightExpr, context),
                StatementUtil.execute(_leftExpr, context)));
    }

    protected abstract Object doOperate(Object right, Object left);
}
