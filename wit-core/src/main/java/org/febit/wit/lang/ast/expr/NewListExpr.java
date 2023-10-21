// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.expr;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.AstUtils;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;

import java.util.List;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public final class NewListExpr implements Expression {

    private final Expression[] valueExprs;
    @Getter
    private final Position position;

    @Override
    public Object execute(final InternalContext context) {
        return List.of(
                context.visit(this.valueExprs)
        );
    }

    @Override
    public Object getConstValue() {
        return List.of(
                AstUtils.calcConstArray(this.valueExprs)
        );
    }
}
