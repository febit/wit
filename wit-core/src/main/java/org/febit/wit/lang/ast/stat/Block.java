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
public final class Block implements IBlock, Loopable {

    private final int indexer;
    private final Statement[] statements;
    private final LoopMeta[] possibleLoops;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(final InternalContext context) {
        return context.pushIndexer(indexer, this::execute0);
    }

    @Nullable
    private Object execute0(final InternalContext context) {
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

    @Override
    public int getVarIndexer() {
        return indexer;
    }

    @Override
    public Statement[] getStatements() {
        return statements;
    }
}
