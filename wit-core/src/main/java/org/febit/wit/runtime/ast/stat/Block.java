// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.stat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.AstUtils;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.LoopFlag;
import org.febit.wit.runtime.Loopable;
import org.febit.wit.runtime.Position;
import org.febit.wit.runtime.ast.IBlock;
import org.febit.wit.runtime.ast.Statement;
import org.jspecify.annotations.Nullable;

import java.util.List;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class Block implements IBlock, Loopable {

    @Getter
    private final int varIndexer;
    @Getter
    private final Statement[] statements;
    private final LoopFlag[] possibleLoops;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        context.heap().onFrame(varIndexer,
                () -> context.visitAndCheckLoop(statements)
        );
        return null;
    }

    @Override
    public List<LoopFlag> collectLoopFlags() {
        return AstUtils.asList(possibleLoops);
    }

    @Override
    public boolean hasLoopFlags() {
        return true;
    }
}
