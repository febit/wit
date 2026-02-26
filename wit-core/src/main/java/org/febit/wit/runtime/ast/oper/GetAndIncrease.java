// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.oper;

import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.ALU;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AssignableExpression;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.jspecify.annotations.Nullable;

public record GetAndIncrease(
        AssignableExpression target,
        Position position
) implements Expression {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        var targetObj = this.target;
        try {
            var value = targetObj.execute(context);
            targetObj.assign(context, ALU.plusOne(value));
            return value;
        } catch (Exception e) {
            throw ScriptEvaluateException.from(e, this);
        }
    }
}
