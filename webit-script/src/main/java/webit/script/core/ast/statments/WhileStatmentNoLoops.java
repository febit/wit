// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Expression;
import webit.script.util.ALU;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class WhileStatmentNoLoops extends AbstractStatment {

    private final Expression whileExpr;
    private final BlockStatment bodyStatment;
    private final boolean doWhileAtFirst;

    public WhileStatmentNoLoops(Expression whileExpr, BlockStatment bodyStatment, boolean doWhileAtFirst, int line, int column) {
        super(line, column);
        this.whileExpr = whileExpr;
        this.bodyStatment = bodyStatment;
        this.doWhileAtFirst = doWhileAtFirst;
    }

    public Object execute(final Context context) {
        boolean go = doWhileAtFirst ? ALU.toBoolean(StatmentUtil.execute(whileExpr, context)) : true;

        while (go) {
            bodyStatment.execute(context);
            go = ALU.toBoolean(StatmentUtil.execute(whileExpr, context));
        }

        return null;
    }
}
