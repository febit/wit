// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import java.util.List;
import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statment;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.ast.loop.Loopable;
import webit.script.util.ALU;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class IfStatment extends AbstractStatment implements Loopable {

    private final Expression ifExpr;
    private final Statment thenStatment;

    public IfStatment(Expression ifExpr, Statment thenStatment, int line, int column) {
        super(line, column);
        this.ifExpr = ifExpr;
        this.thenStatment = thenStatment;
    }

    public Object execute(final Context context) {
        if (ALU.toBoolean(StatmentUtil.execute(ifExpr, context))) {
            StatmentUtil.execute(thenStatment, context);
        }
        return null;
    }

    public List<LoopInfo> collectPossibleLoopsInfo() {
        return StatmentUtil.collectPossibleLoopsInfo(thenStatment);
    }
}
