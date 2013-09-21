// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.Expression;
import webit.script.core.ast.method.MethodDeclare;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class FunctionExecuteExpression extends AbstractExpression {

    private final Expression funcExpr;
    private final Expression[] paramExprs;

    public FunctionExecuteExpression(Expression funcExpr, Expression[] paramExprs, int line, int column) {
        super(line, column);
        this.funcExpr = funcExpr;
        this.paramExprs = paramExprs;
    }

    public Object execute(final Context context) {

        final Object funcObject = StatmentUtil.execute(funcExpr, context);

        if (funcObject instanceof MethodDeclare) {
            final Object[] args = new Object[paramExprs.length];

            for (int i = 0; i < args.length; i++) {
                args[i] = StatmentUtil.execute(paramExprs[i], context);
            }

            return ((MethodDeclare) funcObject).execute(context, args);
        } else {
            throw new ScriptRuntimeException("not a function");
        }
    }
}
