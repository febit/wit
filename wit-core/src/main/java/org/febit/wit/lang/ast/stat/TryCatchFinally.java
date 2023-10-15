// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.LoopMeta;
import org.febit.wit.lang.Loopable;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.AstUtils;
import org.febit.wit.lang.ast.Statement;

import java.util.List;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public class TryCatchFinally implements Statement, Loopable {

    private final Statement tryStat;
    private final int exceptionVarIndex;
    private final Statement catchStat;
    private final Statement finalStat;
    @Getter
    private final Position position;


    @Override
    @Nullable
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
    public List<LoopMeta> collectPossibleLoops() {
        return AstUtils.collectPossibleLoops(tryStat, catchStat, finalStat);
    }
}
