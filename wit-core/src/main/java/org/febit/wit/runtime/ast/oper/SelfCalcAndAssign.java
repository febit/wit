// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.oper;

import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AssignableExpression;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.jspecify.annotations.Nullable;

import java.util.function.BinaryOperator;

public record SelfCalcAndAssign(
        AssignableExpression target,
        Expression delta,
        BinaryOperator<@Nullable Object> operator,
        Position position
) implements Expression {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        try {
            var targetObj = this.target;
            // Must execute right expr first!
            var deltaObj = delta.execute(context);
            return targetObj.assign(context,
                    operator.apply(targetObj.execute(context), deltaObj)
            );
        } catch (Exception e) {
            throw ScriptEvaluateException.from(e, this);
        }
    }

}
