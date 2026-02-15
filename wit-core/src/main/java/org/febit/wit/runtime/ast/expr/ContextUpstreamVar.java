// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.expr;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AssignableExpression;
import org.febit.wit.runtime.ast.Position;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class ContextUpstreamVar implements AssignableExpression {

    private final int page;
    private final int index;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        return context.heap().getFromUpstream(page, index);
    }

    @Override
    @Nullable
    public Object setValue(InternalContext context, @Nullable final Object value) {
        context.heap().setUpstream(page, index, value);
        return value;
    }
}
