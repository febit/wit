// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.expr;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.AstUtils;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.Position;
import org.febit.wit.runtime.ast.Expression;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class NewArrayExpr implements Expression {

    private final Expression[] valueExprs;
    @Getter
    private final Position position;

    @Override
    public Object execute(InternalContext context) {
        return context.visit(this.valueExprs);
    }

    @Override
    public Object evalAsConst() {
        return AstUtils.evalConstArray(this.valueExprs);
    }
}
