// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.statements;

import org.febit.wit.core.ast.Statement;

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
