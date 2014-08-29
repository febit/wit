// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.core.VariantIndexer;
import webit.script.core.ast.Statement;

/**
 *
 * @author Zqq
 */
public abstract class IBlock extends Statement {

    public IBlock(int line, int column) {
        super(line, column);
    }

    public abstract VariantIndexer getVarIndexer();

    public abstract Statement[] getStatements();

    public abstract boolean hasLoops();
}
