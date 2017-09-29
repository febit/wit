// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast;

import org.febit.wit.InternalContext;
import org.febit.wit.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public abstract class SelfOperator extends Expression {

    protected final AssignableExpression leftExpr;
    protected final Expression rightExpr;

    public SelfOperator(AssignableExpression leftExp, Expression rightExp, int line, int column) {
        super(line, column);
        this.leftExpr = leftExp;
        this.rightExpr = rightExp;
    }

    @Override
    public final Object execute(final InternalContext context) {
        try {
            AssignableExpression left = this.leftExpr;
            return left.setValue(context, doOperate(rightExpr.execute(context),
                    left.execute(context)));
        } catch (Exception e) {
            throw StatementUtil.castToScriptRuntimeException(e, this);
        }
    }

    protected abstract Object doOperate(Object right, Object left);
}
