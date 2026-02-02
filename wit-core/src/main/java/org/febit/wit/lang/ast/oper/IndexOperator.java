// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.oper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.InternalContext;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.AssignableExpression;
import org.febit.wit.lang.ast.Expression;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
@RequiredArgsConstructor
public class IndexOperator implements AssignableExpression {

    private final Expression leftExpr;
    private final Expression rightExpr;

    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        try {
            return context.getBeanProperty(leftExpr.execute(context), rightExpr.execute(context));
        } catch (Exception e) {
            throw ScriptRuntimeException.from(e, this);
        }
    }

    @Override
    @Nullable
    public Object setValue(InternalContext context, @Nullable final Object value) {
        try {
            context.setBeanProperty(
                    leftExpr.execute(context),
                    rightExpr.execute(context),
                    value);
            return value;
        } catch (Exception e) {
            throw ScriptRuntimeException.from(e, this);
        }
    }
}
