// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.Expression;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.lang.MethodDeclare;
import webit.script.util.ExceptionUtil;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public final class MethodExecute extends Expression {

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
            final Expression[] paramExprs = this.paramExprs;
            int i = 0;
            final int len;
            final Object[] results = new Object[len = paramExprs.length];
            try {
                for (i = 0; i < len; i++) {
                    results[i] = paramExprs[i].execute(context);
                }
            } catch (Exception e) {
                throw ExceptionUtil.castToScriptRuntimeException(e, paramExprs[i]);
            }
            return ((MethodDeclare) funcObject).invoke(context, results);
        }
        throw new ScriptRuntimeException("not a function");
    }
}
