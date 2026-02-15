// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.stat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.expr.FunctionDeclareExpr;
import org.febit.wit.runtime.iter.Iter;
import org.febit.wit.runtime.iter.IterMethodFilter;
import org.febit.wit.util.Iters;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class ForInNoLoops implements Statement {

    @Nullable
    private final FunctionDeclareExpr filterFuncDeclare;
    private final Expression collectionExpr;
    private final int indexer;
    private final int iterIndex;
    private final int itemIndex;
    private final Statement[] statements;
    @Nullable
    private final Statement elseStatement;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        Iter iter = iter(context);
        if (iter.hasNext()) {
            context.heap().onFrame(indexer, () -> execute0(context, iter));
            return null;
        }
        if (elseStatement != null) {
            elseStatement.execute(context);
        }
        return null;
    }

    private Iter iter(InternalContext context) {
        var iter = Iters.toIter(collectionExpr.execute(context), this);
        if (filterFuncDeclare == null) {
            return iter;
        }
        return new IterMethodFilter(context, filterFuncDeclare.execute(context), iter);
    }

    @SuppressWarnings({
            "UnnecessaryLocalVariable",
            "squid:S3776", // Cognitive Complexity of methods should not be too high
    })
    private void execute0(InternalContext context, Iter iter) {
        var stats = this.statements;
        var itemIdx = this.itemIndex;
        var heap = context.heap();
        heap.set(iterIndex, iter);
        do {
            heap.set(itemIdx, iter.next());
            context.visit(stats);
        } while (iter.hasNext());
    }
}
