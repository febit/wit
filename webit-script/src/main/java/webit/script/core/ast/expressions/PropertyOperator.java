// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.Expression;
import webit.script.core.ast.ResetableValue;
import webit.script.core.ast.ResetableValueExpression;
import webit.script.resolvers.ResolverManager;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class PropertyOperator extends AbstractExpression implements ResetableValueExpression {

    private final Expression expr;
    private final String property;

    public PropertyOperator(Expression expr, String property, int line, int column) {
        super(line, column);
        this.expr = expr;
        this.property = property;
    }

    public Object execute(final Context context, final boolean needReturn) {
        return context.resolverManager.get(StatmentUtil.execute(expr, context), property);
    }

    public ResetableValue getResetableValue(Context context) {
        final Object parent = StatmentUtil.execute(expr, context);
        final ResolverManager resolverManager = context.resolverManager;
        return new ResetableValue() {
            public Object get() {
                return resolverManager.get(parent, property);
            }

            public boolean set(Object value) {
                return resolverManager.set(parent, property, value);
            }
        };
    }
}
