// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.expr;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.Position;
import org.febit.wit.runtime.ast.Expression;
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
