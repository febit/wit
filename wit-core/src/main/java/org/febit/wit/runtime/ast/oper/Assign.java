// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.oper;

import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AssignableExpression;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.jspecify.annotations.Nullable;

public record Assign(
        AssignableExpression target,
        Expression right,
        Position position
) implements Expression {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        return target.set(context, right.execute(context));
    }
}
