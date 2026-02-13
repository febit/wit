// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.stat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.Position;
import org.febit.wit.runtime.ast.IBlock;
import org.febit.wit.runtime.ast.Statement;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
@RequiredArgsConstructor
public class BlockWithoutLoops implements IBlock {

    @Getter
    private final int varIndexer;
    @Getter
    private final Statement[] statements;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        context.heap().onFrame(varIndexer,
                () -> context.visit(statements)
        );
        return null;
    }

    @Override
    public boolean hasLoopFlags() {
        return false;
    }

    @Override
    public Statement optimize() {
        return statements.length == 0 ? NoopStatement.INSTANCE : this;
    }
}
