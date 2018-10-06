// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.expressions;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.debug.BreakpointListener;
import org.febit.wit.util.StatementUtil;

/**
 * @author zqq90
 */
public class BreakpointExpression extends Expression {

    private final BreakpointListener listener;
    private final Object label;
    private final Expression expression;

    public BreakpointExpression(BreakpointListener listener, Object label,
                                Expression expression, int line, int column) {
        super(line, column);
        this.listener = listener;
        this.label = label;
        this.expression = StatementUtil.optimize(expression);
    }

    @Override
    public Object execute(InternalContext context) {
        Object result = expression.execute(context);
        listener.onBreakpoint(label, context, this, result);
        return result;
    }

    public Expression getExpression() {
        return expression;
    }
}
