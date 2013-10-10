// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import java.util.LinkedList;
import java.util.List;
import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Expression;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.ast.loop.Loopable;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class ReturnStatment extends AbstractStatment implements Loopable {

    private final Expression expr;

    public ReturnStatment(Expression expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    public Object execute(final Context context) {
        context.loopCtrl.returnLoop(
                expr != null
                ? StatmentUtil.execute(expr, context)
                : Context.VOID);
        return null;
    }

    public List<LoopInfo> collectPossibleLoopsInfo() {
        LinkedList<LoopInfo> list;
        (list = new LinkedList<LoopInfo>()).add(new LoopInfo(LoopInfo.RETURN, 0, line, column));
        return list;
    }
}
