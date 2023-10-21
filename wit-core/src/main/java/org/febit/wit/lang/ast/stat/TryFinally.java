// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.AstUtils;
import org.febit.wit.lang.LoopMeta;
import org.febit.wit.lang.Loopable;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Statement;

import java.util.List;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public class TryFinally implements Statement, Loopable {

    private final Statement tryStat;
    private final Statement finalStat;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(final InternalContext context) {
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
    public List<LoopMeta> collectPossibleLoops() {
        return AstUtils.collectPossibleLoops(tryStat, finalStat);
    }

}
