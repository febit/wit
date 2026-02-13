// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.expr;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.FrameIndexer;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.Position;
import org.febit.wit.runtime.Undefined;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.method.FunctionFunctionDeclare;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class FunctionDeclareExpr implements Expression {

    private final Object[] argDefaults;
    private final int varSize;
    private final FrameIndexer[] indexers;
    private final Statement[] statements;
    private final int start;
    private final boolean hasReturnLoops;
    @Getter
    private final Position position;

    @Override
    public FunctionFunctionDeclare execute(InternalContext context) {
        return new FunctionFunctionDeclare(this, context, indexers, this.varSize);
    }

    @Nullable
    public Object apply(InternalContext context, @Nullable Object @Nullable [] args) {
        fillArgs(context, args);
        if (hasReturnLoops) {
            context.visitAndCheckLoop(statements);
            return context.resetReturnLoop();
        } else {
            context.visit(statements);
            return Undefined.UNDEFINED;
        }
    }

    private void fillArgs(InternalContext context, @Nullable Object @Nullable [] args) {
        var heap = context.heap();

        var copyIdx = this.start;
        heap.set(copyIdx++, args);

        var defaults = this.argDefaults;
        var total = defaults.length;
        if (total == 0) {
            return;
        }

        int i = 0;
        // Fill passed args
        if (args != null) {
            int len = Math.min(total, args.length);
            for (; i < len; i++) {
                var arg = args[i];
                heap.set(copyIdx++, arg != null ? arg : defaults[i]);
            }
        }
        // Fill defaults
        for (; i < total; i++) {
            heap.set(copyIdx++, defaults[i]);
        }
    }
}
