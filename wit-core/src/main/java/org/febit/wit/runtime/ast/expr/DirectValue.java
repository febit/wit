// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.expr;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class DirectValue implements Expression {

    @Nullable
    public final Object value;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        return value;
    }

    @Override
    @Nullable
    public Object evalAsConst() {
        return value;
    }
}
