// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.operators;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.AssignableExpression;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.util.ExceptionUtil;

/**
 * @author zqq90
 */
public final class PropertyOperator extends AssignableExpression {

    private final Expression expr;
    private final String property;

    public PropertyOperator(Expression expr, String property, int line, int column) {
        super(line, column);
        this.expr = expr;
        this.property = property;
    }

    @Override
    public Object execute(final InternalContext context) {
        try {
            return context.getBeanProperty(
                    expr.execute(context),
                    property);
        } catch (Exception e) {
            throw ExceptionUtil.toScriptRuntimeException(e, this);
        }
    }

    @Override
    public Object setValue(final InternalContext context, final Object value) {
        try {
            context.setBeanProperty(
                    expr.execute(context),
                    property, value);
            return value;
        } catch (Exception e) {
            throw ExceptionUtil.toScriptRuntimeException(e, this);
        }
    }
}
