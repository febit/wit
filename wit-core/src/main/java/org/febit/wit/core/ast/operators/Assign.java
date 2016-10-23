// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.operators;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.core.ast.ResetableValueExpression;

/**
 *
 * @author zqq90
 */
public final class Assign extends Expression {

    private final Expression rexpr;
    private final ResetableValueExpression lexpr;

    public Assign(ResetableValueExpression lexpr, Expression rexpr, int line, int column) {
        super(line, column);
        this.lexpr = lexpr;
        this.rexpr = rexpr;
    }

    @Override
    public Object execute(final InternalContext context) {
        return lexpr.setValue(context, rexpr.execute(context));
    }
}
