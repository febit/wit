// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.Context;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;

/**
 *
 * @author zqq90
 */
public final class Echo extends Statement {

    private final Expression expr;

    public Echo(Expression expr) {
        super(expr.line, expr.column);
        this.expr = expr;
    }

    public Echo(Expression expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    @Override
    public Object execute(final Context context) {
        context.out(expr.execute(context));
        return null;
    }
}
