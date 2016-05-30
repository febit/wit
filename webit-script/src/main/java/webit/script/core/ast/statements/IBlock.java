// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.core.ast.Statement;

/**
 *
 * @author zqq90
 */
public abstract class IBlock extends Statement {

    public IBlock(int line, int column) {
        super(line, column);
    }

    public abstract int getVarIndexer();

    public abstract Statement[] getStatements();

    public abstract boolean hasLoops();
}
