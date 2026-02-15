// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.oper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AssignableExpression;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
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
            throw ScriptEvaluateException.from(e, this);
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
            throw ScriptEvaluateException.from(e, this);
        }
    }
}
