// Copyright (c) 2013, Webit Team. All Rights Reserved.

package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.BinaryOperator;
import webit.script.core.ast.Expression;
import webit.script.core.ast.ResetableValue;
import webit.script.core.ast.ResetableValueExpression;
import webit.script.resolvers.ResolverManager;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class IndexOperator extends BinaryOperator implements ResetableValueExpression {

    public IndexOperator(Expression leftExpr, Expression rightExpr, int line, int column) {
        super(leftExpr, rightExpr, line, column);
    }

    @Override
    public Object execute(Context context, boolean needReturn) {
        return context.resolverManager.get(StatmentUtil.execute(leftExpr, context), StatmentUtil.execute(rightExpr, context));
    }

    public ResetableValue getResetableValue(Context context) {
        final Object bean = StatmentUtil.execute(leftExpr, context);
        final Object key = StatmentUtil.execute(rightExpr, context);
        final ResolverManager resolverManager = context.resolverManager;
        return new ResetableValue() {
            public Object get() {
                return resolverManager.get(bean, key);
            }

            public boolean set(Object value) {
                return resolverManager.set(bean, key, value);
            }
        };
    }
}
