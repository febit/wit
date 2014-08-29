// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.operators;

import webit.script.Context;
import webit.script.core.ast.Expression;
import webit.script.core.ast.ResetableValueExpression;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public final class PropertyOperator extends ResetableValueExpression {

    private final Expression expr;
    private final String property;

    public PropertyOperator(Expression expr, String property, int line, int column) {
        super(line, column);
        this.expr = expr;
        this.property = property;
    }

    public Object execute(final Context context) {
        return context.resolverManager.get(
                StatementUtil.execute(expr, context),
                property);
    }

    public Object setValue(final Context context, final Object value) {
        context.resolverManager.set(
                StatementUtil.execute(expr, context),
                property, value);
        return value;
    }
}
