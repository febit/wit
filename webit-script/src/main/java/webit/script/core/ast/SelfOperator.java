// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast;

import webit.script.Context;
import webit.script.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public abstract class SelfOperator extends Expression {

    protected final ResetableValueExpression leftExpr;
    protected final Expression rightExpr;

    public SelfOperator(ResetableValueExpression leftExp, Expression rightExp, int line, int column) {
        super(line, column);
        this.leftExpr = leftExp;
        this.rightExpr = rightExp;
    }

    @Override
    public final Object execute(final Context context) {
        try {
            ResetableValueExpression left = this.leftExpr;
            return left.setValue(context, doOperate(rightExpr.execute(context),
                    left.execute(context)));
        } catch (Exception e) {
            throw StatementUtil.castToScriptRuntimeException(e, this);
        }
    }

    protected abstract Object doOperate(Object right, Object left);
}
