// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core;

import org.febit.wit.runtime.ast.AstUtils;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.stat.TryCatchFinally;
import org.febit.wit.runtime.ast.stat.TryFinally;
import org.jspecify.annotations.Nullable;

public class TryPart {

    private final Position position;

    private final Statement tryBlock;
    private int exceptionVarIndex;

    @Nullable
    private Statement catchBlock;
    @Nullable
    private Statement finalBlock;

    public TryPart(Statement tryBlock, Position position) {
        this.position = position;
        this.tryBlock = AstUtils.optimize(tryBlock);
    }

    public TryPart setCatchStat(int exceptionVarIndex, Statement catchStat) {
        this.exceptionVarIndex = exceptionVarIndex;
        this.catchBlock = AstUtils.optimize(catchStat);
        return this;
    }

    public TryPart setFinalStat(Statement finalBlock) {
        this.finalBlock = AstUtils.optimize(finalBlock);
        return this;
    }

    public Statement pop() {
        if (catchBlock == null) {
            return this.finalBlock != null
                    ? new TryFinally(tryBlock, finalBlock, position)
                    : this.tryBlock;
        }

        return new TryCatchFinally(tryBlock, exceptionVarIndex, catchBlock, finalBlock, position);
    }
}
