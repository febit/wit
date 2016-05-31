// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;


import webit.script.Context;
import webit.script.core.ast.Constable;
import webit.script.core.ast.Expression;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.lang.MethodDeclare;
import webit.script.lang.UnConstableMethodDeclare;
import webit.script.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public final class MethodExecute extends Expression implements Constable {

    private final Expression funcExpr;
    private final Expression[] paramExprs;

    public MethodExecute(Expression funcExpr, Expression[] paramExprs, int line, int column) {
        super(line, column);
        this.funcExpr = funcExpr;
        this.paramExprs = paramExprs;
    }

    @Override
    public Object execute(final Context context) {
        final Object func = funcExpr.execute(context);
        if (!(func instanceof MethodDeclare)) {
            throw new ScriptRuntimeException("not a function", this);
        }
        final Expression[] exprs = this.paramExprs;
        final int len = exprs.length;
        final Object[] results = new Object[len];
        for (int i = 0; i < len; i++) {
            results[i] = exprs[i].execute(context);
        }
        return ((MethodDeclare) func).invoke(context, results);
    }

    @Override
    public Object getConstValue() {

        final Object func = StatementUtil.calcConst(funcExpr, true);
        if (!(func instanceof MethodDeclare)) {
            throw new ScriptRuntimeException("not a function", this);
        }
        if (func instanceof UnConstableMethodDeclare) {
            return Context.VOID;
        }

        final Object[] params = StatementUtil.calcConstArrayForce(paramExprs);

        return ((MethodDeclare) func).invoke(null, params);
    }
}
