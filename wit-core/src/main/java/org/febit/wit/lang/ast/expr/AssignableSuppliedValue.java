// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.expr;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.AssignableExpression;
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
}
