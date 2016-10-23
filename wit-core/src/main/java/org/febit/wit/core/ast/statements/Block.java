// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.statements;

import java.util.Arrays;
import java.util.LinkedList;
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
public final class Block extends IBlock implements Loopable {

    public final int indexer;
    public final Statement[] statements;
    public final LoopInfo[] possibleLoopsInfo;

    public Block(int indexer, Statement[] statements, LoopInfo[] possibleLoopsInfo, int line, int column) {
        super(line, column);
        this.indexer = indexer;
        this.statements = statements;
        this.possibleLoopsInfo = possibleLoopsInfo;
    }

    @Override
    public Object execute(final InternalContext context) {
        final int preIndex = context.indexer;
        context.indexer = indexer;
        StatementUtil.executeInvertedAndCheckLoops(statements, context);
        context.indexer = preIndex;
        return null;
    }

    @Override
    public List<LoopInfo> collectPossibleLoopsInfo() {
        return new LinkedList<>(Arrays.asList(possibleLoopsInfo));
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
