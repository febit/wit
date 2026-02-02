// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.oper;

import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AstUtils;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.expr.DirectValue;
import org.jspecify.annotations.Nullable;

import java.util.function.BinaryOperator;

public record ConstableBiOperator(
        Expression left,
        Expression right,
        BinaryOperator<@Nullable Object> operator,
        Position position
) implements Expression {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        try {
            return operator.apply(
                    left.execute(context),
                    right.execute(context)
            );
        } catch (Exception e) {
            throw ScriptEvaluateException.from(e, this);
        }
    }

    @Override
    public Expression optimize() {
        if (AstUtils.isImmutableDirectValue(left)
                && AstUtils.isImmutableDirectValue(right)) {
            return new DirectValue(
                    operator.apply(
                            ((DirectValue) left).value(),
                            ((DirectValue) right).value()
                    ),
                    position
            );
        }
        return this;
    }

    @Override
    @Nullable
    public Object evalAsConst() {
        return operator.apply(
                AstUtils.evalConst(left),
                AstUtils.evalConst(right)
        );
    }
}
