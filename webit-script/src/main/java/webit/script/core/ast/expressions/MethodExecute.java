// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.Expression;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.method.MethodDeclare;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public final class MethodExecute extends AbstractExpression {

    private final Expression funcExpr;
    private final Expression[] paramExprs;

    public MethodExecute(Expression funcExpr, Expression[] paramExprs, int line, int column) {
        super(line, column);
        this.funcExpr = funcExpr;
        this.paramExprs = paramExprs;
    }

    public Object execute(final Context context) {
        final Object funcObject;
        if ((funcObject = StatementUtil.execute(funcExpr, context)) instanceof MethodDeclare) {
            return ((MethodDeclare) funcObject).invoke(context, StatementUtil.execute(paramExprs, context));
        } else {
            throw new ScriptRuntimeException("not a function");
        }
    }
}
