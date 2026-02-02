// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.AstUtils;
import org.febit.wit.lang.LoopMeta;
import org.febit.wit.lang.Loopable;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Statement;
import org.jspecify.annotations.Nullable;

import java.util.List;

@Accessors(fluent = true)
@RequiredArgsConstructor
public class TryFinally implements Statement, Loopable {

    private final Statement tryBlock;
    @Nullable
    private final Statement finalBlock;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        try {
            tryBlock.execute(context);
        } finally {
            if (finalBlock != null) {
                finalBlock.execute(context);
            }
        }
        return null;
    }

    @Override
    public List<LoopMeta> collectPossibleLoops() {
        return AstUtils.collectPossibleLoops(tryBlock, finalBlock);
    }

}
