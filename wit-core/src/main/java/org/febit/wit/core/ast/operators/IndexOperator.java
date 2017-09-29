// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.operators;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.core.ast.AssignableExpression;
import org.febit.wit.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public final class IndexOperator extends AssignableExpression {

    public final Expression leftExpr;
    public final Expression rightExpr;

    public IndexOperator(Expression leftExp, Expression rightExp, int line, int column) {
        super(line, column);
        this.leftExpr = leftExp;
        this.rightExpr = rightExp;
    }

    @Override
    public Object execute(final InternalContext context) {
        try {
            return context.getBeanProperty(leftExpr.execute(context), rightExpr.execute(context));
        } catch (Exception e) {
            throw StatementUtil.castToScriptRuntimeException(e, this);
        }
    }

    @Override
    public Object setValue(final InternalContext context, final Object value) {
        try {
            context.setBeanProperty(
                    leftExpr.execute(context),
                    rightExpr.execute(context),
                    value);
            return value;
        } catch (Exception e) {
            throw StatementUtil.castToScriptRuntimeException(e, this);
        }
    }
}
