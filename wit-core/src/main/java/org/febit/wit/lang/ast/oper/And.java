// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.oper;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.expr.DirectValue;
import org.febit.wit.util.ALU;
import org.febit.wit.util.StatementUtil;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public final class And implements Expression {

    private final Expression leftExpr;
    private final Expression rightExpr;

    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(final InternalContext context) {
        Object left = leftExpr.execute(context);
        return ALU.isTrue(left)
                ? rightExpr.execute(context)
                : left;
    }

    @Override
    public Expression optimize() {
        if (!StatementUtil.isImmutableDirectValue(leftExpr)) {
            return this;
        }
        if (StatementUtil.isImmutableDirectValue(rightExpr)) {
            return new DirectValue(
                    ALU.and(((DirectValue) leftExpr).value, ((DirectValue) rightExpr).value),
                    position);
        } else {
            return ALU.not(((DirectValue) leftExpr).value)
                    ? leftExpr : rightExpr;
        }
    }
}
