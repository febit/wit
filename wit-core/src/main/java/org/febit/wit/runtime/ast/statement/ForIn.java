// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.statement;

import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.FlowControl;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.WithFlowControl;
import org.febit.wit.runtime.ast.expr.FunctionDeclarer;
import org.febit.wit.runtime.iter.Iter;
import org.febit.wit.runtime.iter.IterMethodFilter;
import org.febit.wit.util.Iters;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public record ForIn(
        int label,
        int frame,
        Expression collection,
        @Nullable FunctionDeclarer filter,
        int iterIndex,
        int itemIndex,
        List<StatementBatch> body,
        @Nullable Statement elseBody,
        List<FlowControl> bubbledFlowControls,
        Position position
) implements Statement, WithFlowControl {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        Iter iter = iter(context);
        if (iter.hasNext()) {
            context.variables().onFrame(frame, () -> execute0(context, iter));
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
        var batches = this.body;
        var myLabel = this.label;
        var itemIdx = this.itemIndex;
        var heap = context.variables();
        heap.set(iterIndex, iter);
        do {
            heap.set(itemIdx, iter.next());
            if (context.visitLoopBody(batches, myLabel)) {
                // End this loop if not continue
                break;
            }
        } while (iter.hasNext());
    }

    @Override
    public void bubbleFlowControls(Consumer<FlowControl> collector) {
        bubbledFlowControls.forEach(collector);
    }
}
