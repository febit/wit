// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.expr;

import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AssignableExpression;
import org.febit.wit.runtime.ast.Position;
import org.jspecify.annotations.Nullable;

public record VariableHeapValue(
        int index,
        Position position
) implements AssignableExpression {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        return context.variables().get(index);
    }

    @Override
    @Nullable
    public Object assign(InternalContext context, @Nullable Object value) {
        context.variables().set(index, value);
        return value;
    }
}
