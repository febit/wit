// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.Statement;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public final class Interpolation implements Statement {

    private final Expression expr;
    @Getter
    private final Position position;

    public Interpolation(Expression expr) {
        this(expr, expr.getPosition());
    }

    @Override
    @Nullable
    public Object execute(final InternalContext context) {
        context.out(expr.execute(context));
        return null;
    }
}
