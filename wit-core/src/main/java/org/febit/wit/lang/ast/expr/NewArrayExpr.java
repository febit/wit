// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.expr;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.AstUtils;
import org.febit.wit.lang.ast.Expression;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public final class NewArrayExpr implements Expression {

    private final Expression[] valueExprs;
    @Getter
    private final Position position;

    @Override
    public Object execute(final InternalContext context) {
        return context.visit(this.valueExprs);
    }

    @Override
    public Object getConstValue() {
        return AstUtils.calcConstArray(this.valueExprs);
    }
}
