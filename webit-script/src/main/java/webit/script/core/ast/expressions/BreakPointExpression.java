// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.Expression;
import webit.script.debug.BreakPointListener;

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
        this.expression = expression;
    }

    @Override
    public Object execute(Context context) {
        Object result = expression.execute(context);
        listener.onBreak(label, context, this, result);
        return result;
    }
}
