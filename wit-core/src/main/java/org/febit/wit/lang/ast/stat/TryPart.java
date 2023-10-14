// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Statement;
import org.febit.wit.util.StatementUtil;

/**
 * @author zqq90
 */
public class TryPart {

    private final Position position;

    protected final Statement tryStat;
    protected int exceptionVarIndex;
    protected Statement catchStat;
    protected Statement finalStat;

    public TryPart(Statement tryStat, Position position) {
        this.position = position;
        this.tryStat = StatementUtil.optimize(tryStat);
    }

    public TryPart setCatchStat(int exceptionVarIndex, Statement catchStat) {
        this.exceptionVarIndex = exceptionVarIndex;
        this.catchStat = StatementUtil.optimize(catchStat);
        return this;
    }

    public TryPart setFinalStat(Statement finalStat) {
        this.finalStat = StatementUtil.optimize(finalStat);
        return this;
    }

    public Statement pop() {
        if (tryStat == null) {
            return this.finalStat != null
                    ? this.finalStat
                    : NoopStatement.INSTANCE;
        }

        if (catchStat == null) {
            return this.finalStat != null
                    ? new TryFinally(tryStat, finalStat, position)
                    : this.tryStat;
        }

        return new TryCatchFinally(tryStat, exceptionVarIndex, catchStat, finalStat, position);
    }
}
