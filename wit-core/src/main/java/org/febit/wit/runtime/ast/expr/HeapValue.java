// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.expr;

import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AssignableExpression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.heap.Heap;
import org.jspecify.annotations.Nullable;

public record HeapValue(
        Heap heap,
        String name,
        Position position
) implements AssignableExpression {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        return this.heap.get(name);
    }

    @Override
    @Nullable
    public Object set(InternalContext context, @Nullable Object value) {
        this.heap.set(name, value);
        return value;
    }
}
