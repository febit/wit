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
public final class While implements Statement, Loopable {

    private final Expression condition;
    private final int frame;
    private final Statement[] body;
    private final LoopFlag[] loopFlags;
    private final int label;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        context.heap().onFrame(frame, () -> execute0(context));
        return null;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private void execute0(InternalContext context) {
        var statements = this.body;
        var myLabel = this.label;
        var cond = this.condition;
        var loop = context.loop();
        label:
        while (ALU.isTruly(cond.execute(context))) {
            context.visitAndCheckLoop(statements);
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
        }
    }

    @Override
    public List<LoopFlag> collectLoopFlags() {
        return AstUtils.asList(loopFlags);
    }
}
