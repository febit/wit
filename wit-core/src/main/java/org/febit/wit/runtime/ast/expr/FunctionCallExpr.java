// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.expr;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.Undefined;
import org.febit.wit.runtime.ast.AstUtils;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.function.FunctionDeclare;
import org.febit.wit.runtime.function.UnConstableFunctionDeclare;
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
            throw new ScriptEvaluateException("not a function", this);
        }
        var results = context.visit(this.paramExprs);
        return ((FunctionDeclare) func).apply(context, results);
    }

    @Override
    @Nullable
    public Object evalAsConst() {
        var func = AstUtils.evalConst(funcExpr);
        if (!(func instanceof FunctionDeclare)) {
            throw new ScriptEvaluateException("not a function", this);
        }
        if (func instanceof UnConstableFunctionDeclare) {
            return Undefined.UNDEFINED;
        }
        var params = AstUtils.evalConstArray(paramExprs);
        return ((FunctionDeclare) func).apply(null, params);
    }
}
