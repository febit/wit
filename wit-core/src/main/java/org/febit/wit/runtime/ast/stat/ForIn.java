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
import org.febit.wit.runtime.iter.Iter;
import org.febit.wit.runtime.iter.IterMethodFilter;
import org.febit.wit.util.Iters;
import org.jspecify.annotations.Nullable;

import java.util.List;

@Accessors(fluent = true)
@RequiredArgsConstructor
public class ForIn implements Statement, Loopable {

    @Nullable
    private final FunctionDeclareExpr functionDeclareExpr;

    private final Expression collectionExpr;
    private final int indexer;
    private final int iterIndex;
    private final int itemIndex;
    private final Statement[] statements;
    private final LoopFlag[] loopFlags;

    @Nullable
    private final Statement elseStatement;
    private final int label;
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
        if (functionDeclareExpr == null) {
            return iter;
        }
        return new IterMethodFilter(context, functionDeclareExpr.execute(context), iter);
    }

    @SuppressWarnings({
            "UnnecessaryLocalVariable",
            "squid:S3776", // Cognitive Complexity of methods should not be too high
    })
    private void execute0(InternalContext context, Iter iter) {
        var stats = this.statements;
        var myLabel = this.label;
        var itemIdx = this.itemIndex;
        var loop = context.loop();
        var heap = context.heap();
        heap.set(iterIndex, iter);
        label:
        do {
            heap.set(itemIdx, iter.next());
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
