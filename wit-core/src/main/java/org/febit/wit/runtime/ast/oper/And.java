// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.oper;

import org.febit.wit.runtime.ALU;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AstUtils;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.expr.DirectValue;
import org.jspecify.annotations.Nullable;

public record And(
        Expression left,
        Expression right,
        Position position
) implements Expression {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        var leftObj = left.execute(context);
        return ALU.isTruly(leftObj)
                ? right.execute(context)
                : leftObj;
    }

    @Override
    public Expression optimize() {
        if (!AstUtils.isImmutableDirectValue(left)) {
            return this;
        }
        if (AstUtils.isImmutableDirectValue(right)) {
            return new DirectValue(
                    ALU.and(((DirectValue) left).value(), ((DirectValue) right).value()),
                    position);
        } else {
            return ALU.not(((DirectValue) left).value())
                    ? left : right;
        }
    }
}
