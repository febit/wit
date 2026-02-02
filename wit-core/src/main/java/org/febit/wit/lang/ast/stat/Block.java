// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.AstUtils;
import org.febit.wit.lang.LoopMeta;
import org.febit.wit.lang.Loopable;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.IBlock;
import org.febit.wit.lang.ast.Statement;
import org.jspecify.annotations.Nullable;

import java.util.List;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class Block implements IBlock, Loopable {

    @Getter
    private final int varIndexer;
    @Getter
    private final Statement[] statements;
    private final LoopMeta[] possibleLoops;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        return context.pushIndexer(varIndexer, this::execute0);
    }

    @Nullable
    private Object execute0(InternalContext context) {
        context.visitAndCheckLoop(statements);
        return null;
    }

    @Override
    public List<LoopMeta> collectPossibleLoops() {
        return AstUtils.asList(possibleLoops);
    }

    @Override
    public boolean hasLoops() {
        return true;
    }
}
