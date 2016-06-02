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
public class TryFinally extends Statement implements Loopable {

    private final Statement tryStat;
    private final Statement finalStat;

    public TryFinally(Statement tryStat, Statement finalStat, int line, int column) {
        super(line, column);
        this.tryStat = tryStat;
        this.finalStat = finalStat;
    }

    @Override
    public Object execute(InternalContext context) {
        try {
            tryStat.execute(context);
        } finally {
            if (finalStat != null) {
                finalStat.execute(context);
            }
        }
        return null;
    }

    @Override
    public List<LoopInfo> collectPossibleLoopsInfo() {
        return StatementUtil.collectPossibleLoopsInfo(tryStat, finalStat);
    }

}
