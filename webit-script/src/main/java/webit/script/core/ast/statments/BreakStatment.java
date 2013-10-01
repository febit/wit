// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import java.util.LinkedList;
import java.util.List;
import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.ast.loop.LoopType;
import webit.script.core.ast.loop.Loopable;

/**
 *
 * @author Zqq
 */
public final class BreakStatment extends AbstractStatment implements Loopable {

    private final String label;

    public BreakStatment(String label, int line, int column) {
        super(line, column);
        this.label = label;
    }

    public Object execute(final Context context) {
        context.loopCtrl.breakLoop(label, this);
        return null;
    }

    public List<LoopInfo> collectPossibleLoopsInfo() {
        LinkedList<LoopInfo> list;
        (list = new LinkedList<LoopInfo>()).add(new LoopInfo(LoopType.BREAK, label, line, column));
        return list;
    }
}
