// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.oper;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.util.ALU;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public final class IfOperator implements Expression {

    private final Expression ifExpr;
    private final Expression leftValueExpr;
    private final Expression rightValueExpr;

    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(final InternalContext context) {
        return (ALU.isTrue(ifExpr.execute(context)) ? leftValueExpr : rightValueExpr)
                .execute(context);
    }
}
