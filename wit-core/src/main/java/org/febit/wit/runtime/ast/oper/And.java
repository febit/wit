// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.oper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.ALU;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AstUtils;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.expr.DirectValue;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class And implements Expression {

    private final Expression leftExpr;
    private final Expression rightExpr;

    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        Object left = leftExpr.execute(context);
        return ALU.isTruly(left)
                ? rightExpr.execute(context)
                : left;
    }

    @Override
    public Expression optimize() {
        if (!AstUtils.isImmutableDirectValue(leftExpr)) {
            return this;
        }
        if (AstUtils.isImmutableDirectValue(rightExpr)) {
            return new DirectValue(
                    ALU.and(((DirectValue) leftExpr).value, ((DirectValue) rightExpr).value),
                    position);
        } else {
            return ALU.not(((DirectValue) leftExpr).value)
                    ? leftExpr : rightExpr;
        }
    }
}
