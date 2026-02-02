// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.expr;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.AssignableExpression;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class ScopedContextVar implements AssignableExpression {

    private final int scope;
    private final int index;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        return context.getParentScopeValue(scope, index);
    }

    @Override
    @Nullable
    public Object setValue(InternalContext context, @Nullable final Object value) {
        context.setParentScopeValue(scope, index, value);
        return value;
    }
}
