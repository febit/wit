// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import java.util.LinkedList;
import java.util.List;
import webit.script.Context;
import webit.script.core.LoopInfo;
import webit.script.core.ast.Loopable;
import webit.script.core.ast.Statement;

/**
 *
 * @author zqq90
 */
public final class Break extends Statement implements Loopable {

    private final int label;

    public Break(int label, int line, int column) {
        super(line, column);
        this.label = label;
    }

    @Override
    public Object execute(final Context context) {
        context.breakLoop(label);
        return null;
    }

    @Override
    public List<LoopInfo> collectPossibleLoopsInfo() {
        LinkedList<LoopInfo> list;
        (list = new LinkedList<>()).add(new LoopInfo(LoopInfo.BREAK, label, line, column));
        return list;
    }
}
