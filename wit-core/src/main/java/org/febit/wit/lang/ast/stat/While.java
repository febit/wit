// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.ALU;
import org.febit.wit.lang.AstUtils;
import org.febit.wit.lang.LoopMeta;
import org.febit.wit.lang.Loopable;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.Statement;
import org.jspecify.annotations.Nullable;

import java.util.List;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class While implements Statement, Loopable {

    private final Expression whileExpr;
    private final int indexer;
    private final Statement[] statements;
    private final LoopMeta[] possibleLoops;
    private final int label;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        return context.pushIndexer(indexer, this::execute0);
    }

    @Nullable
    @SuppressWarnings("UnnecessaryLocalVariable")
    private Object execute0(InternalContext context) {
        var stats = this.statements;
        var myLabel = this.label;
        var condition = this.whileExpr;
        label:
        while (ALU.isTruly(condition.execute(context))) {
            context.visitAndCheckLoop(stats);
            if (context.loopKind().isNoop()) {
                continue;
            }
            if (!context.matchLabel(myLabel)) {
                break; //while
            }
            switch (context.loopKind()) {
                case BREAK:
                    context.resetLoop();
                    break label; // while
                case RETURN:
                    //can't deal
                    break label; //while
                case CONTINUE:
                    context.resetLoop();
                    break; //switch
                default:
                    break label; //while
            }
        }
        return null;
    }

    @Override
    public List<LoopMeta> collectPossibleLoops() {
        return AstUtils.asList(possibleLoops);
    }
}
