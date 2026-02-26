// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.statement;

import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.LoopFlag;
import org.febit.wit.runtime.ast.Loopable;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.jspecify.annotations.Nullable;

import java.util.List;

public record Break(
        int label,
        Position position
) implements Statement, Loopable {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        context.loop().toBreak(label);
        return null;
    }

    @Override
    public List<LoopFlag> collectLoopFlags() {
        return List.of(
                new LoopFlag(LoopFlag.Kind.BREAK, label, position)
        );
    }
}
