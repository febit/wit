// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import java.util.List;
import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Optimizable;
import webit.script.core.ast.Statment;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.ast.loop.Loopable;
import webit.script.util.ALU;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class IfElseStatment extends AbstractStatment implements Loopable {

    private final Expression ifExpr;
    private final Statment thenStatment;
    private final Statment elseStatment;

    public IfElseStatment(Expression ifExpr, Statment thenStatment, Statment elseStatment, int line, int column) {
        super(line, column);
        this.ifExpr = ifExpr;
        this.thenStatment = thenStatment;
        this.elseStatment = elseStatment;
    }

    public Object execute(final Context context) {
        if (ALU.toBoolean(StatmentUtil.execute(ifExpr, context))) {
            StatmentUtil.execute(thenStatment, context);
        } else {
            StatmentUtil.execute(elseStatment, context);
        }
        return null;
    }

    public List<LoopInfo> collectPossibleLoopsInfo() {

        List<LoopInfo> list = StatmentUtil.collectPossibleLoopsInfo(thenStatment);
        List<LoopInfo> list2 = StatmentUtil.collectPossibleLoopsInfo(elseStatment);

        if (list == null) {
            return list2;
        } else if (list2 != null) {
            list.addAll(list2);
        }
        return list;
    }
}
