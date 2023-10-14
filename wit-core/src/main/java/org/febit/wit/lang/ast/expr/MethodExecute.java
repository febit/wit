// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.expr;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.MethodDeclare;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.UnConstableMethodDeclare;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.util.StatementUtil;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public final class MethodExecute implements Expression {

    private final Expression funcExpr;
    private final Expression[] paramExprs;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(final InternalContext context) {
        var func = funcExpr.execute(context);
        if (!(func instanceof MethodDeclare)) {
            throw new ScriptRuntimeException("not a function", this);
        }
        var results = context.execute(this.paramExprs);
        return ((MethodDeclare) func).invoke(context, results);
    }

    @Override
    @Nullable
    public Object getConstValue() {
        var func = StatementUtil.calcConst(funcExpr);
        if (!(func instanceof MethodDeclare)) {
            throw new ScriptRuntimeException("not a function", this);
        }
        if (func instanceof UnConstableMethodDeclare) {
            return InternalContext.VOID;
        }
        var params = StatementUtil.calcConstArray(paramExprs);
        return ((MethodDeclare) func).invoke(null, params);
    }
}
