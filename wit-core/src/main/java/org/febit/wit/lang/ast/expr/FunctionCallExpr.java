// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.expr;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.AstUtils;
import org.febit.wit.lang.FunctionDeclare;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.UnConstableFunctionDeclare;
import org.febit.wit.lang.ast.Expression;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public final class FunctionCallExpr implements Expression {

    private final Expression funcExpr;
    private final Expression[] paramExprs;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(final InternalContext context) {
        var func = funcExpr.execute(context);
        if (!(func instanceof FunctionDeclare)) {
            throw new ScriptRuntimeException("not a function", this);
        }
        var results = context.visit(this.paramExprs);
        return ((FunctionDeclare) func).invoke(context, results);
    }

    @Override
    @Nullable
    public Object getConstValue() {
        var func = AstUtils.calcConst(funcExpr);
        if (!(func instanceof FunctionDeclare)) {
            throw new ScriptRuntimeException("not a function", this);
        }
        if (func instanceof UnConstableFunctionDeclare) {
            return InternalContext.VOID;
        }
        var params = AstUtils.calcConstArray(paramExprs);
        return ((FunctionDeclare) func).invoke(null, params);
    }
}
