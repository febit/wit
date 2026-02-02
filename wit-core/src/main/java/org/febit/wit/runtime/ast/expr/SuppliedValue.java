// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.expr;

import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public record SuppliedValue(
        Supplier<?> factory,
        Position position
) implements Expression {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        return this.factory.get();
    }
}
