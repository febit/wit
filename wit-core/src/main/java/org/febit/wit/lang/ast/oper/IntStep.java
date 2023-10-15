// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.oper;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ALU;
import org.febit.wit.util.CollectionUtil;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public class IntStep implements Expression {

    private final Expression leftExpr;
    private final Expression rightExpr;

    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(final InternalContext context) {
        final int left = ALU.requireNumber(leftExpr.execute(context)).intValue();
        final int right = ALU.requireNumber(rightExpr.execute(context)).intValue();
        if (left < right) {
            return CollectionUtil.createIntAscIter(left, right);
        } else {
            return CollectionUtil.createIntDescIter(left, right);
        }
    }
}
