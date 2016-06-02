// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.InternalContext;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;

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
        context.out(expr.execute(context));
        return null;
    }
}
