// Copyright (c) 2013, Webit Team. All Rights Reserved.

package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.Expression;
import webit.script.debug.BreakPointListener;
import webit.script.util.StatementUtil;

/**
 *
 * @author zqq
 */
public class BreakPointExpression  extends AbstractExpression {

    private final BreakPointListener listener;
    private final String label;
    private final Expression expression;

    public BreakPointExpression(BreakPointListener listener, String label, Expression expression, int line, int column) {
        super(line, column);
        this.listener = listener;
        this.label = label;
        this.expression = expression;
    }
    
    public Object execute(Context context) {
        Object result = StatementUtil.execute(expression, context);
        listener.onBreak(label, context, this, result);
        return result;
    }
}
