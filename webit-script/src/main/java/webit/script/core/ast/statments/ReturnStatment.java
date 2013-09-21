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
public final class ReturnStatment extends AbstractStatment {

    private final Expression expr;

    public ReturnStatment(Expression expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    public Object execute(final Context context) {
        context.loopCtrl.returnLoop(
                expr != null
                ? StatmentUtil.execute(expr, context)
                : Context.VOID,
                this);
        return null;
    }
}
