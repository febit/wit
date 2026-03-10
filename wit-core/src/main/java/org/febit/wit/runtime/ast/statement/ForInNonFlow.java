// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.statement;

import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.expr.FunctionDeclarer;
import org.febit.wit.runtime.iter.Iter;
import org.febit.wit.runtime.iter.IterMethodFilter;
import org.febit.wit.util.Iters;
import org.jspecify.annotations.Nullable;

public record ForInNonFlow(
        int scope,
        Expression collection,
        @Nullable FunctionDeclarer filter,
        int iterIndex,
        int itemIndex,
        StatementBatch body,
        @Nullable Statement elseBody,
        Position position
) implements Statement {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        Iter iter = iter(context);
        if (iter.hasNext()) {
            context.variables().onScope(scope, () -> execute0(context, iter));
            return null;
        }
        if (elseBody != null) {
            elseBody.execute(context);
        }
        return null;
    }

    private Iter iter(InternalContext context) {
        var iter = Iters.toIter(collection.execute(context), this);
        if (filter == null) {
            return iter;
        }
        return new IterMethodFilter(context, filter.execute(context), iter);
    }

    @SuppressWarnings({
            "UnnecessaryLocalVariable",
            "squid:S3776", // Cognitive Complexity of methods should not be too high
    })
    private void execute0(InternalContext context, Iter iter) {
        var batch = this.body;
        var itemIdx = this.itemIndex;
        var heap = context.variables();
        heap.set(iterIndex, iter);
        do {
            heap.set(itemIdx, iter.next());
            batch.execute(context);
        } while (iter.hasNext());
    }
}
