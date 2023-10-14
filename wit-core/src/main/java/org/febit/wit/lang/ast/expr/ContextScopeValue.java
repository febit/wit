// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.expr;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.AssignableExpression;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public final class ContextScopeValue implements AssignableExpression {

    private final int scope;
    private final int index;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(final InternalContext context) {
        return context.getParentScopeValue(scope, index);
    }

    @Override
    @Nullable
    public Object setValue(final InternalContext context, @Nullable final Object value) {
        context.setParentScopeValue(scope, index, value);
        return value;
    }
}
