// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.stat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.ALU;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AstUtils;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.LoopFlag;
import org.febit.wit.runtime.ast.Loopable;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.jspecify.annotations.Nullable;

import java.util.List;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class DoWhile implements Statement, Loopable {

    private final Expression whileExpr;
    private final int indexer;
    private final Statement[] statements;
    private final LoopFlag[] loopFlags;
    private final int label;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        context.heap().onFrame(indexer, () -> execute0(context));
        return null;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private void execute0(InternalContext context) {
        var stats = this.statements;
        var myLabel = this.label;
        var condition = this.whileExpr;
        var loop = context.loop();
        label:
        do {
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
        } while (ALU.isTruly(condition.execute(context)));
    }

    @Override
    public List<LoopFlag> collectLoopFlags() {
        return AstUtils.asList(loopFlags);
    }
}
