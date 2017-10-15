// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.expressions;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.debug.BreakPointListener;
import org.febit.wit.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public class BreakPointExpression extends Expression {

    private final BreakPointListener listener;
    private final Object label;
    private final Expression expression;

    public BreakPointExpression(BreakPointListener listener, Object label, Expression expression, int line, int column) {
        super(line, column);
        this.listener = listener;
        this.label = label;
        this.expression = StatementUtil.optimize(expression);
    }

    @Override
    public Object execute(InternalContext context) {
        Object result = expression.execute(context);
        listener.onBreak(label, context, this, result);
        return result;
    }
}
