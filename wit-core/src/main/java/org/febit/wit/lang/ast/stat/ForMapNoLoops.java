// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.KeyIter;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.Statement;
import org.febit.wit.lang.ast.expr.FunctionDeclareExpr;
import org.febit.wit.lang.iter.KeyIterMethodFilter;
import org.febit.wit.util.Iters;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class ForMapNoLoops implements Statement {

    @Nullable
    private final FunctionDeclareExpr filterFuncDeclare;
    private final Expression mapExpr;
    private final int indexer;
    private final int iterIndex;
    private final int keyIndex;
    private final int valueIndex;
    private final Statement[] statements;
    @Nullable
    private final Statement elseBlock;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        var iter = iter(context);
        if (iter.hasNext()) {
            return context.pushIndexer(indexer, c -> this.execute0(c, iter));
        }
        if (elseBlock != null) {
            elseBlock.execute(context);
        }
        return null;
    }

    private KeyIter iter(InternalContext context) {
        var iter = Iters.toKeyIter(mapExpr.execute(context), this);
        if (filterFuncDeclare == null) {
            return iter;
        }
        return new KeyIterMethodFilter(context, filterFuncDeclare.execute(context), iter);
    }

    @Nullable
    @SuppressWarnings({
            "UnnecessaryLocalVariable",
            "squid:S3776", // Cognitive Complexity of methods should not be too high
    })
    private Object execute0(InternalContext context, KeyIter iter) {
        var stats = this.statements;
        var keyIdx = this.keyIndex;
        var valIdx = this.valueIndex;
        var vars = context.vars;
        vars[iterIndex] = iter;
        do {
            vars[keyIdx] = iter.next();
            vars[valIdx] = iter.value();
            context.visit(stats);
        } while (iter.hasNext());
        return null;
    }
}
