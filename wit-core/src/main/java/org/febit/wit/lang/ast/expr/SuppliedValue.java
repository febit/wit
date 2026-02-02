// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.expr;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class SuppliedValue implements Expression {

    private final Supplier<?> supplier;

    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        return this.supplier.get();
    }
}
