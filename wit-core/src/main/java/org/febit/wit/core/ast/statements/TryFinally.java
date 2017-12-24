// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.statements;

import java.util.List;
import org.febit.wit.InternalContext;
import org.febit.wit.core.LoopInfo;
import org.febit.wit.core.ast.Loopable;
import org.febit.wit.core.ast.Statement;
import org.febit.wit.util.StatementUtil;

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
    public List<LoopInfo> collectPossibleLoops() {
        return StatementUtil.collectPossibleLoops(tryStat, finalStat);
    }

}
