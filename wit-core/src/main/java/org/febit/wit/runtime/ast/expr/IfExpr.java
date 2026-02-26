// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.expr;

import org.febit.wit.runtime.ALU;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.jspecify.annotations.Nullable;

public record IfExpr(
        Expression condition,
        Expression left,
        Expression right,
        Position position
) implements Expression {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        return (ALU.isTruly(condition.execute(context)) ? left : right)
                .execute(context);
    }
}
