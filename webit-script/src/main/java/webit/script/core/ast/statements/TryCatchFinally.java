// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import java.util.List;
import webit.script.InternalContext;
import webit.script.core.LoopInfo;
import webit.script.core.ast.Loopable;
import webit.script.core.ast.Statement;
import webit.script.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public class TryCatchFinally extends Statement implements Loopable {

    private final Statement tryStat;
    private final int exceptionVarIndex;
    private final Statement catchStat;
    private final Statement finalStat;

    public TryCatchFinally(Statement tryStat, int exceptionVarIndex, Statement catchStat, Statement finalStat, int line, int column) {
        super(line, column);
        this.tryStat = tryStat;
        this.exceptionVarIndex = exceptionVarIndex;
        this.catchStat = catchStat;
        this.finalStat = finalStat;
    }

    @Override
    public Object execute(InternalContext context) {
        try {
            tryStat.execute(context);
        } catch (Exception e) {
            context.vars[exceptionVarIndex] = e;
            catchStat.execute(context);
        } finally {
            if (finalStat != null) {
                finalStat.execute(context);
            }
        }
        return null;
    }

    @Override
    public List<LoopInfo> collectPossibleLoopsInfo() {
        return StatementUtil.collectPossibleLoopsInfo(tryStat, catchStat, finalStat);
    }
}
