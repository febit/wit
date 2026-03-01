// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.expr;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.Undefined;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.FrameIndexer;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.function.ScriptFunction;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class FunctionDeclarer implements Expression {

    private final Object[] argDefaults;
    private final int frameSize;
    private final FrameIndexer[] indexers;
    private final Statement[] statements;
    private final int argsIndexStart;
    private final boolean hasReturnFlow;
    @Getter
    private final Position position;

    @Override
    public ScriptFunction execute(InternalContext context) {
        return new ScriptFunction(this, context, indexers, this.frameSize);
    }

    @Nullable
    public Object apply(InternalContext context, @Nullable Object @Nullable [] args) {
        fillArgs(context, args);
        if (hasReturnFlow) {
            context.visit(statements);
            var flow = context.flow();
            var returned = flow.returned();
            flow.reset();
            return returned;
        } else {
            context.visitNonFlow(statements);
            return Undefined.UNDEFINED;
        }
    }

    private void fillArgs(InternalContext context, @Nullable Object @Nullable [] args) {
        var heap = context.variables();

        var copyIdx = this.argsIndexStart;
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
