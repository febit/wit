// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.expr;

import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AssignableExpression;
import org.febit.wit.runtime.ast.Position;
import org.jspecify.annotations.Nullable;

public record ContextPageVar(
        int page,
        int index,
        Position position
) implements AssignableExpression {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        return context.heap().getAtPage(page, index);
    }

    @Override
    @Nullable
    public Object set(InternalContext context, @Nullable Object value) {
        context.heap().setAtPage(page, index, value);
        return value;
    }
}
