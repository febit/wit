// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.expr;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.StatementUtils;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class NewArray implements Expression {

    private final Expression[] values;
    @Getter
    private final Position position;

    @Override
    public Object execute(InternalContext context) {
        return context.visit(this.values);
    }

    @Override
    public Object evalAsConst() {
        return StatementUtils.evalConstArray(this.values);
    }
}
