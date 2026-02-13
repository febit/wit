// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.stat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.KeyIter;
import org.febit.wit.runtime.Position;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.expr.FunctionDeclareExpr;
import org.febit.wit.runtime.iter.KeyIterMethodFilter;
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
        var keyIdx = this.keyIndex;
        var valIdx = this.valueIndex;
        var heap = context.heap();
        heap.set(iterIndex, iter);
        do {
            heap.set(
                    keyIdx, iter.next(),
                    valIdx, iter.value()
            );
            context.visit(stats);
        } while (iter.hasNext());
    }
}
