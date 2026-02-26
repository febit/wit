// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.statement;

import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AstUtils;
import org.febit.wit.runtime.ast.LoopFlag;
import org.febit.wit.runtime.ast.Loopable;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.jspecify.annotations.Nullable;

import java.util.List;

public record TryFinally(
        Statement body,
        @Nullable Statement finalBody,
        Position position
) implements Statement, Loopable {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        try {
            body.execute(context);
        } finally {
            if (finalBody != null) {
                finalBody.execute(context);
            }
        }
        return null;
    }

    @Override
    public List<LoopFlag> collectLoopFlags() {
        return AstUtils.collectLoopFlags(body, finalBody);
    }

}
