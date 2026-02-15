// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.expr;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
@RequiredArgsConstructor
public class BreakpointExpr implements Expression {

    @Nullable
    private final Object label;
    @Getter
    private final Expression expression;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        Object result = expression.execute(context);
        context.handleBreakpoint(label, this, result);
        return result;
    }

}
