// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.expr;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.Context;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.VariantIndexer;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.Statement;
import org.febit.wit.lang.method.FunctionFunctionDeclare;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class FunctionDeclareExpr implements Expression {

    private final Object[] argDefaults;
    private final int varSize;
    private final VariantIndexer[] indexers;
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
    public Object invoke(InternalContext context, @Nullable Object @Nullable [] args) {
        fillArgs(context, args);
        if (hasReturnLoops) {
            context.visitAndCheckLoop(statements);
            return context.resetReturnLoop();
        } else {
            context.visit(statements);
            return Context.VOID;
        }
    }

    private void fillArgs(InternalContext context, @Nullable Object @Nullable [] args) {
        var vars = context.vars;

        var copyIdx = this.start;
        vars[copyIdx++] = args;

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
                vars[copyIdx++] = arg != null ? arg : defaults[i];
            }
        }
        // Fill defaults
        for (; i < total; i++) {
            vars[copyIdx++] = defaults[i];
        }
    }
}
