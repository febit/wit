// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.oper;

import org.febit.wit.runtime.ALU;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.util.Iters;

public record IntStep(
        Expression from,
        Expression to,
        Position position
) implements Expression {

    @Override
    public Object execute(InternalContext context) {
        var left = ALU.requireNumber(from.execute(context)).intValue();
        var right = ALU.requireNumber(to.execute(context)).intValue();
        return left < right
                ? Iters.asc(left, right)
                : Iters.desc(left, right);
    }
}
