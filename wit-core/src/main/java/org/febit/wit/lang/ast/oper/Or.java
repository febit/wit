// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.oper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.ALU;
import org.febit.wit.lang.AstUtils;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.expr.DirectValue;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class Or implements Expression {

    private final Expression leftExpr;
    private final Expression rightExpr;

    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        Object left = leftExpr.execute(context);
        return ALU.isTruly(left)
                ? left
                : rightExpr.execute(context);
    }

    @Override
    public Expression optimize() {
        if (!AstUtils.isImmutableDirectValue(leftExpr)) {
            return this;
        }
        if (AstUtils.isImmutableDirectValue(rightExpr)) {
            return new DirectValue(
                    ALU.or(((DirectValue) leftExpr).value, ((DirectValue) rightExpr).value),
                    position);
        } else {
            return ALU.isTruly(((DirectValue) leftExpr).value)
                    ? leftExpr : rightExpr;
        }
    }
}
