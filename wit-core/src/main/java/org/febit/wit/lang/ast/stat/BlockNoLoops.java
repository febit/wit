// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.IBlock;
import org.febit.wit.lang.ast.Statement;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
@RequiredArgsConstructor
public class BlockNoLoops implements IBlock {

    @Getter
    private final int varIndexer;
    @Getter
    private final Statement[] statements;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        return context.pushIndexer(varIndexer, this::execute0);
    }

    @Nullable
    private Object execute0(InternalContext context) {
        context.visit(statements);
        return null;
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
