// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.stat;

import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.jspecify.annotations.Nullable;

public record Echo(
        Expression operand,
        Position position
) implements Statement {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        context.out(operand.execute(context));
        return null;
    }
}
