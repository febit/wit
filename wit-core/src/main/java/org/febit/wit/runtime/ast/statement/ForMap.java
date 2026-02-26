// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.statement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AstUtils;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.LoopFlag;
import org.febit.wit.runtime.ast.Loopable;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.expr.FunctionDeclarer;
import org.febit.wit.runtime.iter.KeyIter;
import org.febit.wit.runtime.iter.KeyIterMethodFilter;
import org.febit.wit.util.Iters;
import org.jspecify.annotations.Nullable;

import java.util.List;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class ForMap implements Statement, Loopable {

    @Nullable
    private final FunctionDeclarer filter;
    private final Expression collection;
    private final int frame;
    private final int iterIndex;
    private final int keyIndex;
    private final int valueIndex;
    private final Statement[] statements;
    private final LoopFlag[] loopFlags;
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
        var stats = this.statements;
        var myLabel = this.label;
        var keyIdx = this.keyIndex;
        var valIdx = this.valueIndex;
        var loop = context.loop();
        var heap = context.variables();
        heap.set(iterIndex, iter);
        label:
        do {
            heap.set(
                    keyIdx, iter.next(),
                    valIdx, iter.value()
            );
            context.visitAndCheckLoop(stats);
            if (loop.isNone()) {
                continue;
            }
            if (!context.loop().isTargetLabel(myLabel)) {
                break; //while
            }
            switch (loop.kind()) {
                case BREAK -> {
                    // Reset loop state
                    // Then break to exit the loop
                    context.loop().reset();
                    break label;
                }
                case RETURN -> {
                    break label;
                }
                case CONTINUE -> // Reset loop state, Then continue to next loop iteration
                        context.loop().reset();
                case NONE -> throw new IllegalStateException("unexpected NOOP");
            }
        } while (iter.hasNext());
    }

    @Override
    public List<LoopFlag> collectLoopFlags() {
        return AstUtils.asList(loopFlags);
    }
}
