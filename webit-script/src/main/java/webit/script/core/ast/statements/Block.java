// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import webit.script.Context;
import webit.script.core.VariantIndexer;
import webit.script.core.ast.Statement;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.ast.loop.Loopable;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
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

    public Object execute(final Context context) {
        final int preIndex = context.indexer;
        context.indexer = indexer;
        StatementUtil.executeInvertedAndCheckLoops(statements, context);
        context.indexer = preIndex;
        return null;
    }

    public List<LoopInfo> collectPossibleLoopsInfo() {
        return new LinkedList<LoopInfo>(Arrays.asList(possibleLoopsInfo));
    }

    public boolean hasLoops() {
        return true;
    }

    public int getVarIndexer() {
        return indexer;
    }

    public Statement[] getStatements() {
        return statements;
    }
}
