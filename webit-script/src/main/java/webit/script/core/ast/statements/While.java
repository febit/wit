// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import webit.script.Context;
import webit.script.core.LoopInfo;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Loopable;
import webit.script.core.ast.Statement;
import webit.script.util.ALU;
import webit.script.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public final class While extends Statement implements Loopable {

    private final Expression whileExpr;
    private final int indexer;
    private final Statement[] statements;
    public final LoopInfo[] possibleLoopsInfo;
    private final int label;

    public While(Expression whileExpr, int indexer, Statement[] statements, LoopInfo[] possibleLoopsInfo, int label, int line, int column) {
        super(line, column);
        this.whileExpr = whileExpr;
        this.indexer = indexer;
        this.statements = statements;
        this.possibleLoopsInfo = possibleLoopsInfo;
        this.label = label;
    }

    @Override
    public Object execute(final Context context) {
        final Statement[] stats = this.statements;
        final int preIndex = context.indexer;
        context.indexer = indexer;
        label:
        while (ALU.isTrue(whileExpr.execute(context))) {
            StatementUtil.executeInvertedAndCheckLoops(stats, context);
            if (context.hasLoop()) {
                if (context.matchLabel(label)) {
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
                } else {
                    break;
                }
            }
        }
        context.indexer = preIndex;
        return null;
    }

    @Override
    public List<LoopInfo> collectPossibleLoopsInfo() {
        return possibleLoopsInfo != null ? new LinkedList<>(Arrays.asList(possibleLoopsInfo)) : null;
    }
}
