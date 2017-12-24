// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.statements;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.core.ast.Statement;

/**
 *
 * @author zqq90
 */
public final class Interpolation extends Statement {

    private final Expression expr;

    public Interpolation(Expression expr) {
        super(expr.line, expr.column);
        this.expr = expr;
    }

    public Interpolation(Expression expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    @Override
    public Object execute(final InternalContext context) {
        context.write(expr.execute(context));
        return null;
    }
}
