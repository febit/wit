// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.expr;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.VariantIndexer;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.Statement;
import org.febit.wit.lang.method.FunctionFunctionDeclare;

/**
 * @author zqq90
 */
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
    public FunctionFunctionDeclare execute(final InternalContext context) {
        return new FunctionFunctionDeclare(this, context, indexers, this.varSize);
    }

    @Nullable
    public Object invoke(final InternalContext context, @Nullable final Object[] args) {
        var defaults = this.argDefaults;
        var argsTotalCount = defaults.length;
        var vars = context.vars;
        var argsStart = this.start;
        var len = args != null ? args.length : 0;
        vars[argsStart++] = args;
        if (argsTotalCount != 0) {
            int passedLen = Math.min(argsTotalCount, len);
            int i = 0;
            for (; i < passedLen; i++) {
                Object arg = args[i];
                vars[argsStart++] = arg != null ? arg : defaults[i];
            }
            for (; i < argsTotalCount; i++) {
                vars[argsStart++] = defaults[i];
            }
        }
        if (hasReturnLoops) {
            context.visitAndCheckLoop(statements);
            return context.resetReturnLoop();
        } else {
            context.visit(statements);
            return InternalContext.VOID;
        }
    }
}
