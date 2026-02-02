// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.oper;

import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AssignableExpression;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.jspecify.annotations.Nullable;

import java.util.function.BinaryOperator;

public record SelfOperator(
        AssignableExpression target,
        Expression right,
        BinaryOperator<@Nullable Object> operator,
        Position position
) implements Expression {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        try {
            var assignable = this.target;
            // Must execute right expr first!
            var rightResult = right.execute(context);
            return assignable.set(context,
                    operator.apply(assignable.execute(context), rightResult)
            );
        } catch (Exception e) {
            throw ScriptEvaluateException.from(e, this);
        }
    }

}
