// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.Context;
import webit.script.core.ast.AbstractStatement;
import webit.script.core.ast.Expression;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public final class Interpolation extends AbstractStatement {

    private final Expression expr;

    public Interpolation(Expression expr) {
        super(expr.getLine(), expr.getColumn());
        this.expr = expr;
    }

    public Interpolation(Expression expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    public Object execute(final Context context) {
        context.out(StatementUtil.execute(expr, context));
        return null;
    }
}
