// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.oper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.ALU;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.util.Iters;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
@RequiredArgsConstructor
public class IntStep implements Expression {

    private final Expression leftExpr;
    private final Expression rightExpr;

    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        var left = ALU.requireNumber(leftExpr.execute(context)).intValue();
        var right = ALU.requireNumber(rightExpr.execute(context)).intValue();
        return left < right
                ? Iters.asc(left, right)
                : Iters.desc(left, right);
    }
}
