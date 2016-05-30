// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.Context;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;

/**
 *
 * @author Zqq
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
    public Object execute(final Context context) {
        context.out(expr.execute(context));
        return null;
    }
}
