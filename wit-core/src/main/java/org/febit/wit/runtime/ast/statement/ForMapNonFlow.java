// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.statement;

import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.expr.FunctionDeclarer;
import org.febit.wit.runtime.iter.KeyIter;
import org.febit.wit.runtime.iter.KeyIterMethodFilter;
import org.febit.wit.util.Iters;
import org.jspecify.annotations.Nullable;

public record ForMapNonFlow(
        int frame,
        Expression collection,
        @Nullable FunctionDeclarer filter,
        int iterIndex,
        int keyIndex,
        int valueIndex,
        StatementBatch body,
        @Nullable Statement elseBody,
        Position position
) implements Statement {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        var iter = iter(context);
        if (iter.hasNext()) {
            context.variables().onFrame(frame, () -> execute0(context, iter));
            return null;
        }
        if (elseBody != null) {
            elseBody.execute(context);
        }
        return null;
    }

    private KeyIter iter(InternalContext context) {
        var iter = Iters.toKeyIter(collection.execute(context), this);
        if (filter == null) {
            return iter;
        }
        return new KeyIterMethodFilter(context, filter.execute(context), iter);
    }

    @SuppressWarnings({
            "UnnecessaryLocalVariable",
            "squid:S3776", // Cognitive Complexity of methods should not be too high
    })
    private void execute0(InternalContext context, KeyIter iter) {
        var batch = this.body;
        var keyIdx = this.keyIndex;
        var valIdx = this.valueIndex;
        var heap = context.variables();
        heap.set(iterIndex, iter);
        do {
            heap.set(
                    keyIdx, iter.next(),
                    valIdx, iter.value()
            );
            batch.execute(context);
        } while (iter.hasNext());
    }
}
