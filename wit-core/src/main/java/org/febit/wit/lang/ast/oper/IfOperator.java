// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.oper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.ALU;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class IfOperator implements Expression {

    private final Expression ifExpr;
    private final Expression leftValueExpr;
    private final Expression rightValueExpr;

    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        return (ALU.isTruly(ifExpr.execute(context)) ? leftValueExpr : rightValueExpr)
                .execute(context);
    }
}
