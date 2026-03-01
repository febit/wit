// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.statement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.FlowControl;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.WithFlowControl;
import org.febit.wit.runtime.ast.expr.FunctionDeclarer;
import org.febit.wit.runtime.iter.KeyIter;
import org.febit.wit.runtime.iter.KeyIterMethodFilter;
import org.febit.wit.util.Iters;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class ForMap implements Statement, WithFlowControl {

    @Nullable
    private final FunctionDeclarer filter;
    private final Expression collection;
    private final int frame;
    private final int iterIndex;
    private final int keyIndex;
    private final int valueIndex;
    private final Statement[] body;
    private final List<FlowControl> flowControls;
    @Nullable
    private final Statement elseBody;
    private final int label;
    @Getter
    private final Position position;

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
        var statements = this.body;
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
            if (context.visitLoopFlow(statements, myLabel)) {
                // End this loop if not continue
                break;
            }
        } while (iter.hasNext());
    }

    @Override
    public void collectFlowControls(Consumer<FlowControl> collector) {
        flowControls.forEach(collector);
    }
}
