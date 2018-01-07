// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.expressions;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.InternalVoid;
import org.febit.wit.lang.MethodDeclare;
import org.febit.wit.lang.UnConstableMethodDeclare;
import org.febit.wit.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public final class MethodExecute extends Expression {

    private final Expression funcExpr;
    private final Expression[] paramExprs;

    public MethodExecute(Expression funcExpr, Expression[] paramExprs, int line, int column) {
        super(line, column);
        this.funcExpr = funcExpr;
        this.paramExprs = paramExprs;
    }

    @Override
    public Object execute(final InternalContext context) {
        final Object func = funcExpr.execute(context);
        if (!(func instanceof MethodDeclare)) {
            throw new ScriptRuntimeException("not a function", this);
        }
        final Object[] results = StatementUtil.execute(this.paramExprs, context);
        return ((MethodDeclare) func).invoke(context, results);
    }

    @Override
    public Object getConstValue() {
        final Object func = StatementUtil.calcConst(funcExpr);
        if (!(func instanceof MethodDeclare)) {
            throw new ScriptRuntimeException("not a function", this);
        }
        if (func instanceof UnConstableMethodDeclare) {
            return InternalVoid.VOID;
        }
        final Object[] params = StatementUtil.calcConstArray(paramExprs);
        return ((MethodDeclare) func).invoke(null, params);
    }
}
