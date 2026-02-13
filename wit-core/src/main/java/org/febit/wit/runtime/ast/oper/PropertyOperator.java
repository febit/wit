// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.oper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.Position;
import org.febit.wit.runtime.ast.AssignableExpression;
import org.febit.wit.runtime.ast.Expression;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class PropertyOperator implements AssignableExpression {

    private final Expression expr;
    private final String property;

    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        try {
            return context.getBeanProperty(
                    expr.execute(context),
                    property);
        } catch (Exception e) {
            throw ScriptRuntimeException.from(e, this);
        }
    }

    @Override
    @Nullable
    public Object setValue(InternalContext context, @Nullable final Object value) {
        try {
            context.setBeanProperty(
                    expr.execute(context),
                    property, value);
            return value;
        } catch (Exception e) {
            throw ScriptRuntimeException.from(e, this);
        }
    }
}
