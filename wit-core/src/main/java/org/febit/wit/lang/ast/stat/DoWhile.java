// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.LoopMeta;
import org.febit.wit.lang.Loopable;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.Statement;
import org.febit.wit.lang.ALU;
import org.febit.wit.lang.AstUtils;

import java.util.List;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public final class DoWhile implements Statement, Loopable {

    private final Expression whileExpr;
    private final int indexer;
    private final Statement[] statements;
    private final LoopMeta[] possibleLoops;
    private final int label;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(final InternalContext context) {
        return context.pushIndexer(indexer, this::execute0);
    }

    @Nullable
    @SuppressWarnings("UnnecessaryLocalVariable")
    private Object execute0(final InternalContext context) {
        var stats = this.statements;
        var myLabel = this.label;
        var condition = this.whileExpr;
        label:
        do {
            context.visitAndCheckLoop(stats);
            if (context.noLoop()) {
                continue;
            }
            if (!context.matchLabel(myLabel)) {
                break; //while
            }
            switch (context.getLoopType()) {
                case LoopMeta.BREAK:
                    context.resetLoop();
                    break label; // while
                case LoopMeta.RETURN:
                    //can't deal
                    break label; //while
                case LoopMeta.CONTINUE:
                    context.resetLoop();
                    break; //switch
                default:
                    break label; //while
            }
        } while (ALU.isTrue(condition.execute(context)));
        return null;
    }

    @Override
    public List<LoopMeta> collectPossibleLoops() {
        return AstUtils.asList(possibleLoops);
    }
}
