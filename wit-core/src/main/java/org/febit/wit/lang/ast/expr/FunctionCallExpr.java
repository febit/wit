// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.expr;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.Context;
import org.febit.wit.InternalContext;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.AstUtils;
import org.febit.wit.lang.FunctionDeclare;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.UnConstableFunctionDeclare;
import org.febit.wit.lang.ast.Expression;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class FunctionCallExpr implements Expression {

    private final Expression funcExpr;
    private final Expression[] paramExprs;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        var func = funcExpr.execute(context);
        if (!(func instanceof FunctionDeclare)) {
            throw new ScriptRuntimeException("not a function", this);
        }
        var results = context.visit(this.paramExprs);
        return ((FunctionDeclare) func).invoke(context, results);
    }

    @Override
    @Nullable
    public Object calcAsConst() {
        var func = AstUtils.calcConst(funcExpr);
        if (!(func instanceof FunctionDeclare)) {
            throw new ScriptRuntimeException("not a function", this);
        }
        if (func instanceof UnConstableFunctionDeclare) {
            return Context.VOID;
        }
        var params = AstUtils.calcConstArray(paramExprs);
        return ((FunctionDeclare) func).invoke(null, params);
    }
}
