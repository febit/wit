// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.statement;

import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.FlowControl;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.WithFlowControl;
import org.febit.wit.runtime.ast.expr.FunctionDeclarer;
import org.febit.wit.runtime.iter.Iters;
import org.febit.wit.runtime.iter.KeyIter;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public record ForMap(
        int label,
        int scope,
        Expression collection,
        @Nullable FunctionDeclarer filter,
        int iterIndex,
        int keyIndex,
        int valueIndex,
        List<StatementBatch> body,
        @Nullable Statement elseBody,
        List<FlowControl> bubbledFlowControls,
        Position position
) implements Statement, WithFlowControl {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        var iter = iter(context);
        if (iter.hasNext()) {
            context.variables().onScope(scope, () -> execute0(context, iter));
            return null;
        }
        if (elseBody != null) {
            elseBody.execute(context);
        }
        return null;
    }

    private KeyIter iter(InternalContext context) {
        var iter = Iters.ofKeyIter(collection.execute(context), this);
        if (filter == null) {
            return iter;
        }
        return Iters.ofFiltered(context, iter, filter.execute(context));
    }

    @SuppressWarnings({
            "UnnecessaryLocalVariable",
            "squid:S3776", // Cognitive Complexity of methods should not be too high
    })
    private void execute0(InternalContext context, KeyIter iter) {
        var batches = this.body;
        var myLabel = this.label;
        var keyIdx = this.keyIndex;
        var valIdx = this.valueIndex;
        var heap = context.variables();
        heap.set(iterIndex, iter);
        do {
            heap.set(
                    keyIdx, iter.next(),
                    valIdx, iter.value()
            );
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
