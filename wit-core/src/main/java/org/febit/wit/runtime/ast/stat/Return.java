// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.stat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.Undefined;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.LoopFlag;
import org.febit.wit.runtime.ast.Loopable;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
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
        var result = expr != null
                ? expr.execute(context)
                : Undefined.UNDEFINED;
        context.loop().toReturn(result);
        return null;
    }

    @Override
    public List<LoopFlag> collectLoopFlags() {
        return List.of(
                new LoopFlag(LoopFlag.Kind.RETURN, 0, position)
        );
    }
}
