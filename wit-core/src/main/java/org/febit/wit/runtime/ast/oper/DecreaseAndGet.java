// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.oper;

import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.ALU;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AssignableExpression;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.jspecify.annotations.Nullable;

public record DecreaseAndGet(
        AssignableExpression target,
        Position position
) implements Expression {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        try {
            var assignable = this.target;
            return assignable.set(context, ALU.minusOne(
                    assignable.execute(context)));
        } catch (Exception e) {
            throw ScriptEvaluateException.from(e, this);
        }
    }
}
