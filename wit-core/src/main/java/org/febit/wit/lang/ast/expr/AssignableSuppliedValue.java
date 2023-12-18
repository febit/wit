// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.expr;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.AssignableExpression;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public final class AssignableSuppliedValue implements AssignableExpression {

    private final Supplier<?> supplier;
    private final Consumer<Object> consumer;

    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(final InternalContext context) {
        return this.supplier.get();
    }

    @Override
    @Nullable
    public Object setValue(final InternalContext context, @Nullable final Object value) {
        this.consumer.accept(value);
        return value;
    }
}
