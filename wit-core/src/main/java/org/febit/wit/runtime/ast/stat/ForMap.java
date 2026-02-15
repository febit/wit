// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.stat;

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
import org.febit.wit.runtime.ast.expr.FunctionDeclareExpr;
import org.febit.wit.runtime.iter.KeyIter;
import org.febit.wit.runtime.iter.KeyIterMethodFilter;
import org.febit.wit.util.Iters;
import org.jspecify.annotations.Nullable;

import java.util.List;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class ForMap implements Statement, Loopable {

    @Nullable
    private final FunctionDeclareExpr filterFuncDeclare;
    private final Expression mapExpr;
    private final int indexer;
    private final int iterIndex;
    private final int keyIndex;
    private final int valueIndex;
    private final Statement[] statements;
    private final LoopFlag[] loopFlags;
    @Nullable
    private final Statement elseBlock;
    private final int label;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        var iter = iter(context);
        if (iter.hasNext()) {
            context.heap().onFrame(indexer, () -> execute0(context, iter));
            return null;
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
        var heap = context.heap();
        heap.set(iterIndex, iter);
        label:
        do {
            heap.set(
                    keyIdx, iter.next(),
                    valIdx, iter.value()
            );
            context.visitAndCheckLoop(stats);
            if (loop.isNoop()) {
                continue;
            }
            if (!context.loop().isTargetLabel(myLabel)) {
                break; //while
            }
            switch (loop.kind()) {
                case BREAK:
                    context.loop().reset();
                    break label; // while
                case RETURN:
                    //can't deal
                    break label; //while
                case CONTINUE:
                    context.loop().reset();
                    break; //switch
                default:
                    break label; //while
            }
        } while (iter.hasNext());
    }

    @Override
    public List<LoopFlag> collectLoopFlags() {
        return AstUtils.asList(loopFlags);
    }
}
