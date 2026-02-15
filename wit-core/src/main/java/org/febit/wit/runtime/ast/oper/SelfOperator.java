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

import java.util.function.BinaryOperator;

@Accessors(fluent = true)
@RequiredArgsConstructor
public class SelfOperator implements Expression {

    private final AssignableExpression leftExpr;
    private final Expression rightExpr;
    private final BinaryOperator<@Nullable Object> op;

    @Getter
    private final Position position;

    @Override
    @Nullable
    public final Object execute(InternalContext context) {
        try {
            var assignable = this.leftExpr;
            // Must execute right expr first!
            var rightResult = rightExpr.execute(context);
            return assignable.setValue(context,
                    op.apply(assignable.execute(context), rightResult)
            );
        } catch (Exception e) {
            throw ScriptEvaluateException.from(e, this);
        }
    }

}
