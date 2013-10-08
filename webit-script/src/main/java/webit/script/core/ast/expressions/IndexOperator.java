// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.BinaryOperator;
import webit.script.core.ast.Expression;
import webit.script.core.ast.ResetableValueExpression;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class IndexOperator extends BinaryOperator implements ResetableValueExpression {

    public IndexOperator(Expression leftExpr, Expression rightExpr, int line, int column) {
        super(leftExpr, rightExpr, line, column);
    }

    public Object execute(final Context context) {
        return context.resolverManager.get(StatmentUtil.execute(leftExpr, context), StatmentUtil.execute(rightExpr, context));
    }

    public Object setValue(final Context context, final Object value) {
        context.resolverManager.set(
                StatmentUtil.execute(leftExpr, context),
                StatmentUtil.execute(rightExpr, context),
                value);
        return value;
    }
}
