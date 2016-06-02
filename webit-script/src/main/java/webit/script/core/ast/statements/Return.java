// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import java.util.LinkedList;
import java.util.List;
import webit.script.InternalContext;
import webit.script.core.LoopInfo;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Loopable;
import webit.script.core.ast.Statement;
import webit.script.lang.InternalVoid;

/**
 *
 * @author zqq90
 */
public final class Return extends Statement implements Loopable {

    private final Expression expr;

    public Return(Expression expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    @Override
    public Object execute(final InternalContext context) {
        context.returnLoop(expr != null
                ? expr.execute(context)
                : InternalVoid.VOID);
        return null;
    }

    @Override
    public List<LoopInfo> collectPossibleLoopsInfo() {
        LinkedList<LoopInfo> list;
        (list = new LinkedList<>()).add(new LoopInfo(LoopInfo.RETURN, 0, line, column));
        return list;
    }
}
