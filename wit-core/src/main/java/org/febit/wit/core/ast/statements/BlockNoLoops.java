// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.statements;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.Optimizable;
import org.febit.wit.core.ast.Statement;
import org.febit.wit.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public class BlockNoLoops extends IBlock implements Optimizable {

    private final int indexer;
    private final Statement[] statements;

    public BlockNoLoops(int indexer, Statement[] statements, int line, int column) {
        super(line, column);
        this.indexer = indexer;
        this.statements = statements;
    }

    @Override
    public Object execute(final InternalContext context) {
        final int preIndex = context.indexer;
        context.indexer = indexer;
        StatementUtil.executeInverted(statements, context);
        context.indexer = preIndex;
        return null;
    }

    @Override
    public int getVarIndexer() {
        return indexer;
    }

    @Override
    public Statement[] getStatements() {
        return statements;
    }

    @Override
    public boolean hasLoops() {
        return false;
    }

    @Override
    public Statement optimize(){
        return statements.length == 0 ? null : this;
    }
}
