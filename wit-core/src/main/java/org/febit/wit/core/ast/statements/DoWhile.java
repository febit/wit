// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.statements;

import java.util.List;
import org.febit.wit.InternalContext;
import org.febit.wit.core.LoopInfo;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.core.ast.Loopable;
import org.febit.wit.core.ast.Statement;
import org.febit.wit.util.ALU;
import org.febit.wit.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public final class DoWhile extends Statement implements Loopable {

    private final Expression whileExpr;
    private final int indexer;
    private final Statement[] statements;
    private final LoopInfo[] possibleLoops;
    private final int label;

    public DoWhile(Expression whileExpr, int indexer, Statement[] statements, LoopInfo[] possibleLoops, int label, int line, int column) {
        super(line, column);
        this.whileExpr = whileExpr;
        this.indexer = indexer;
        this.statements = statements;
        this.possibleLoops = possibleLoops;
        this.label = label;
    }

    @Override
    public Object execute(final InternalContext context) {
        final Statement[] stats = this.statements;
        final int myLabel = this.label;
        final int preIndex = context.indexer;
        context.indexer = indexer;
        label:
        do {
            StatementUtil.executeWithLoopCheck(stats, context);
            if (!context.hasLoop()) {
                continue;
            }
            if (!context.matchLabel(myLabel)) {
                break; //while
            }
            switch (context.getLoopType()) {
                case LoopInfo.BREAK:
                    context.resetLoop();
                    break label; // while
                case LoopInfo.RETURN:
                    //can't deal
                    break label; //while
                case LoopInfo.CONTINUE:
                    context.resetLoop();
                    break; //switch
                default:
                    break label; //while
            }
        } while (ALU.isTrue(whileExpr.execute(context)));
        context.indexer = preIndex;
        return null;
    }

    @Override
    public List<LoopInfo> collectPossibleLoops() {
        return StatementUtil.asList(possibleLoops);
    }
}
