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
public final class IfStatment extends AbstractStatment implements Optimizable, Loopable {

    private final Expression ifExpr;
    private final Statment thenStatment;
    private final Statment elseStatment;

    public IfStatment(Expression ifExpr, Statment thenStatment, Statment elseStatment, int line, int column) {
        super(line, column);
        this.ifExpr = ifExpr;
        this.thenStatment = thenStatment;
        this.elseStatment = elseStatment;
    }

    public Object execute(final Context context) {
        if (ALU.toBoolean(StatmentUtil.execute(ifExpr, context))) {
            if (thenStatment != null) {
                StatmentUtil.execute(thenStatment, context);
            }
        } else if (elseStatment != null) {
            StatmentUtil.execute(elseStatment, context);
        }
        return null;
    }

    public Statment optimize() {
        return thenStatment != null || elseStatment != null ? this : null;
    }

    public List<LoopInfo> collectPossibleLoopsInfo() {
        List<LoopInfo> list = null;
        if (thenStatment != null && thenStatment instanceof Loopable) {
            list = ((Loopable) thenStatment).collectPossibleLoopsInfo();
        }

        if (elseStatment != null && elseStatment instanceof Loopable) {
            List<LoopInfo> list2 = ((Loopable) elseStatment).collectPossibleLoopsInfo();

            if (list == null) {
                list = list2;
            } else if (list2 != null) {
                list.addAll(list2);
            }
        }
        return list;
    }
}
