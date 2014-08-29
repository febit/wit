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
public final class IndexOperator extends ResetableValueExpression {

    public final Expression leftExpr;
    public final Expression rightExpr;

    public IndexOperator(Expression leftExp, Expression rightExp, int line, int column) {
        super(line, column);
        this.leftExpr = leftExp;
        this.rightExpr = rightExp;
    }
    
    public Object execute(final Context context) {
        return context.resolverManager.get(StatementUtil.execute(leftExpr, context), StatementUtil.execute(rightExpr, context));
    }

    public Object setValue(final Context context, final Object value) {
        context.resolverManager.set(
                StatementUtil.execute(leftExpr, context),
                StatementUtil.execute(rightExpr, context),
                value);
        return value;
    }
}
