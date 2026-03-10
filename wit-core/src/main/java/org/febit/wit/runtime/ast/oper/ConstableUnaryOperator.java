// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.oper;

import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.StatementUtils;
import org.febit.wit.runtime.ast.expr.DirectValue;
import org.jspecify.annotations.Nullable;

import java.util.function.UnaryOperator;

public record ConstableUnaryOperator(
        Expression target,
        UnaryOperator<@Nullable Object> operator,
        Position position
) implements Expression {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        try {
            return operator.apply(target.execute(context));
        } catch (Exception e) {
            throw ScriptEvaluateException.from(e, this);
        }
    }

    @Override
    public Expression optimize() {
        if (StatementUtils.isImmutableDirectValue(target)) {
            return new DirectValue(operator.apply(((DirectValue) target).value()), position);
        }
        return this;
    }

    @Override
    @Nullable
    public Object evalAsConst() {
        return operator.apply(StatementUtils.evalAsConst(target));
    }
}
