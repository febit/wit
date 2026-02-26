// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.expr;

import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AssignableExpression;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.jspecify.annotations.Nullable;

public record FixedPropertyAccess(
        Expression target,
        String property,
        Position position
) implements AssignableExpression {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        try {
            return context.getBeanProperty(
                    target.execute(context),
                    property);
        } catch (Exception e) {
            throw ScriptEvaluateException.from(e, this);
        }
    }

    @Override
    @Nullable
    public Object assign(InternalContext context, @Nullable final Object value) {
        try {
            context.setBeanProperty(
                    target.execute(context),
                    property, value);
            return value;
        } catch (Exception e) {
            throw ScriptEvaluateException.from(e, this);
        }
    }
}
