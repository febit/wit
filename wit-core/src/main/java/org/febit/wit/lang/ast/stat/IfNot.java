// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.ALU;
import org.febit.wit.lang.AstUtils;
import org.febit.wit.lang.LoopMeta;
import org.febit.wit.lang.Loopable;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.Statement;
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
    public List<LoopMeta> collectPossibleLoops() {
        return AstUtils.collectPossibleLoops(elseBlock);
    }
}
