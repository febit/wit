// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Statement;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public class BlockNoLoops implements IBlock {

    private final int indexer;
    private final Statement[] statements;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(final InternalContext context) {
        return context.pushIndexer(indexer, this::execute0);
    }

    @Nullable
    private Object execute0(final InternalContext context) {
        context.visit(statements);
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
    public Statement optimize() {
        return statements.length == 0 ? NoopStatement.INSTANCE : this;
    }
}
