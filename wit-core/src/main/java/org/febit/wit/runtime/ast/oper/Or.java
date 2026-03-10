// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.oper;

import org.febit.wit.runtime.ALU;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.StatementUtils;
import org.febit.wit.runtime.ast.expr.DirectValue;
import org.jspecify.annotations.Nullable;

public record Or(
        Expression left,
        Expression right,
        Position position
) implements Expression {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        var leftObj = left.execute(context);
        return ALU.isTruly(leftObj)
                ? leftObj
                : right.execute(context);
    }

    @Override
    public Expression optimize() {
        if (!StatementUtils.isImmutableDirectValue(left)) {
            return this;
        }
        if (StatementUtils.isImmutableDirectValue(right)) {
            return new DirectValue(
                    ALU.or(((DirectValue) left).value(), ((DirectValue) right).value()),
                    position);
        } else {
            return ALU.isTruly(((DirectValue) left).value())
                    ? left : right;
        }
    }
}
