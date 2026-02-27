// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.expr;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.Undefined;
import org.febit.wit.runtime.WitFunction;
import org.febit.wit.runtime.ast.AstUtils;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class FunctionCaller implements Expression {

    private final Expression func;
    private final Expression[] params;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        var funcObj = this.func.execute(context);
        if (!(funcObj instanceof WitFunction declare)) {
            throw new ScriptEvaluateException("not a function", this);
        }
        var paramsObj = context.visit(this.params);
        return declare.apply(context, paramsObj);
    }

    @Override
    @Nullable
    public Object evalAsConst() {
        var funcObj = AstUtils.evalConst(this.func);
        if (!(funcObj instanceof WitFunction.Constable constable)) {
            if (!(funcObj instanceof WitFunction)) {
                throw new ScriptEvaluateException("not a function", this);
            }
            return Undefined.UNDEFINED;
        }
        var paramsObj = AstUtils.evalConstArray(this.params);
        return constable.apply(paramsObj);
    }
}
