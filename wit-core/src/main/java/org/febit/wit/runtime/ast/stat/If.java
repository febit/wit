// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.stat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.ALU;
import org.febit.wit.runtime.AstUtils;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.LoopFlag;
import org.febit.wit.runtime.Loopable;
import org.febit.wit.runtime.Position;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Statement;
import org.jspecify.annotations.Nullable;

import java.util.List;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class If implements Statement, Loopable {

    private final Expression ifExpr;
    private final Statement thenBlock;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        if (ALU.isTruly(ifExpr.execute(context))) {
            return thenBlock.execute(context);
        }
        return null;
    }

    @Override
    public List<LoopFlag> collectLoopFlags() {
        return AstUtils.collectLoopFlags(thenBlock);
    }
}
