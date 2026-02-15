// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.stat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class Interpolation implements Statement {

    private final Expression expr;
    @Getter
    private final Position position;

    public Interpolation(Expression expr) {
        this(expr, expr.position());
    }

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        context.out(expr.execute(context));
        return null;
    }
}
