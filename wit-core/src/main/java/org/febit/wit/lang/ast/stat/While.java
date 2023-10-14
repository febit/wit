// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.LoopMeta;
import org.febit.wit.lang.Loopable;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.Statement;
import org.febit.wit.util.ALU;
import org.febit.wit.util.StatementUtil;

import java.util.List;

/**
 * @author zqq90
 */
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
    @SuppressWarnings({
            "squid:LabelsShouldNotBeUsedCheck",
            "squid:S135" // Loops should not contain more than a single "break" or "continue" statement
    })
    public Object execute(final InternalContext context) {
        var stats = this.statements;
        final int myLabel = this.label;
        final int preIndex = context.indexer;
        context.indexer = indexer;
        label:
        while (ALU.isTrue(whileExpr.execute(context))) {
            context.executeWithLoop(stats);
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
        }
        context.indexer = preIndex;
        return null;
    }

    @Override
    public List<LoopMeta> collectPossibleLoops() {
        return StatementUtil.asList(possibleLoops);
    }
}
