// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.operators;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.core.ast.Optimizable;
import org.febit.wit.core.ast.expressions.DirectValue;
import org.febit.wit.util.ALU;

/**
 *
 * @author zqq90
 */
public final class Not extends Expression implements Optimizable {

    private final Expression expr;

    public Not(Expression expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    @Override
    public Object execute(final InternalContext context) {
        return !ALU.isTrue(expr.execute(context));
    }

    @Override
    public Expression optimize() {
        return expr instanceof DirectValue
                ? new DirectValue(ALU.not(((DirectValue) expr).value), line, column)
                : this;
    }
}
