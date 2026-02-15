// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.stat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.ALU;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AstUtils;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.LoopFlag;
import org.febit.wit.runtime.ast.Loopable;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.jspecify.annotations.Nullable;

import java.util.List;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class IfNot implements Statement, Loopable {

    private final Expression ifExpr;
    private final Statement elseBlock;
    @Getter
    private final Position position;


    @Override
    @Nullable
    public Object execute(InternalContext context) {
        if (!ALU.isTruly(ifExpr.execute(context))) {
            return elseBlock.execute(context);
        }
        return null;
    }

    @Override
    public List<LoopFlag> collectLoopFlags() {
        return AstUtils.collectLoopFlags(elseBlock);
    }
}
