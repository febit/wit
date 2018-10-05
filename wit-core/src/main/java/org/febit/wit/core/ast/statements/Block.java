// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.statements;

import org.febit.wit.InternalContext;
import org.febit.wit.core.LoopInfo;
import org.febit.wit.core.ast.Loopable;
import org.febit.wit.core.ast.Statement;
import org.febit.wit.util.StatementUtil;

import java.util.List;

/**
 * @author zqq90
 */
public final class Block extends IBlock implements Loopable {

    private final int indexer;
    private final Statement[] statements;
    private final LoopInfo[] possibleLoops;

    public Block(int indexer, Statement[] statements, LoopInfo[] possibleLoops, int line, int column) {
        super(line, column);
        this.indexer = indexer;
        this.statements = statements;
        this.possibleLoops = possibleLoops;
    }

    @Override
    public Object execute(final InternalContext context) {
        final int preIndex = context.indexer;
        context.indexer = indexer;
        StatementUtil.executeWithLoopCheck(statements, context);
        context.indexer = preIndex;
        return null;
    }

    @Override
    public List<LoopInfo> collectPossibleLoops() {
        return StatementUtil.asList(possibleLoops);
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
