// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import webit.script.Context;
import webit.script.core.VariantIndexer;
import webit.script.core.ast.AbstractStatement;
import webit.script.core.ast.Statement;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.ast.loop.Loopable;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public final class Block extends AbstractStatement implements Loopable, IBlock {

    public final VariantIndexer varIndexer;
    public final Statement[] statements;
    public final LoopInfo[] possibleLoopsInfo;

    public Block(VariantIndexer varIndexer, Statement[] statements, LoopInfo[] possibleLoopsInfo, int line, int column) {
        super(line, column);
        this.varIndexer = varIndexer;
        this.statements = statements;
        this.possibleLoopsInfo = possibleLoopsInfo;
    }

    public Object execute(final Context context) {
        context.push(varIndexer);
        StatementUtil.executeInvertedAndCheckLoops(statements, context);
        context.pop();
        return null;
    }

    public List<LoopInfo> collectPossibleLoopsInfo() {
        return new LinkedList<LoopInfo>(Arrays.asList(possibleLoopsInfo));
    }

    public boolean hasLoops() {
        return true;
    }

    public VariantIndexer getVarIndexer() {
        return varIndexer;
    }

    public Statement[] getStatements() {
        return statements;
    }
}
