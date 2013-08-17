// Copyright (c) 2013, Webit Team. All Rights Reserved.

package webit.script.core.ast.statments;

import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Expression;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class EchoStatment extends AbstractStatment {

    private final Expression expr;

    public EchoStatment(Expression expr) {
        super(expr.getLine(), expr.getColumn());
        this.expr = expr;
    }

    public EchoStatment(Expression expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    @Override
    public void execute(Context context) {
        context.out(StatmentUtil.execute(expr, context));
    }
}
