// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.expr;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AssignableExpression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.heap.StaticHeaps;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class AssignableSuppliedValue implements AssignableExpression {

    private final Supplier<?> supplier;
    private final Consumer<@Nullable Object> consumer;

    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        return this.supplier.get();
    }

    @Override
    @Nullable
    public Object setValue(InternalContext context, @Nullable Object value) {
        this.consumer.accept(value);
        return value;
    }

    public static AssignableSuppliedValue ofStatic(StaticHeaps.StaticHeap heap, String name, Position position) {
        return new AssignableSuppliedValue(
                () -> heap.get(name),
                v -> heap.set(name, v),
                position
        );
    }
}
