// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import java.util.LinkedList;
import java.util.List;
import webit.script.Context;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.ast.loop.Loopable;

/**
 *
 * @author Zqq
 */
public final class Return extends Statement implements Loopable {

    private final Expression expr;

    public Return(Expression expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    public Object execute(final Context context) {
        context.loopCtrl.returnLoop(
                expr != null
                ? expr.execute(context)
                : Context.VOID);
        return null;
    }

    public List<LoopInfo> collectPossibleLoopsInfo() {
        LinkedList<LoopInfo> list;
        (list = new LinkedList<LoopInfo>()).add(new LoopInfo(LoopInfo.RETURN, 0, line, column));
        return list;
    }
}
