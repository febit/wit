// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.statements;

import org.febit.wit.core.ast.Statement;
import org.febit.wit.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public class TryPart {

    protected final int line;
    protected final int column;

    protected final Statement tryStat;
    protected int exceptionVarIndex;
    protected Statement catchStat;
    protected Statement finalStat;

    public TryPart(Statement tryStat, int line, int column) {
        this.line = line;
        this.column = column;
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
                    : NoneStatement.INSTANCE;
        }

        if (catchStat == null) {
            return this.finalStat != null
                    ? new TryFinally(tryStat, finalStat, line, column)
                    : this.tryStat;
        }

        return new TryCatchFinally(tryStat, exceptionVarIndex, catchStat, finalStat, line, column);
    }
}
