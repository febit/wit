// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.Context;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.LoopMeta;
import org.febit.wit.lang.Loopable;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.Statement;
import org.jspecify.annotations.Nullable;

import java.util.List;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class Return implements Statement, Loopable {

    @Nullable
    private final Expression expr;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        context.returnLoop(expr != null
                ? expr.execute(context)
                : Context.VOID);
        return null;
    }

    @Override
    public List<LoopMeta> collectPossibleLoops() {
        return List.of(
                new LoopMeta(LoopMeta.Kind.RETURN, 0, position)
        );
    }
}
