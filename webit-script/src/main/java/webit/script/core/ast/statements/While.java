// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import webit.script.Context;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;
import webit.script.core.ast.loop.LoopCtrl;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.ast.loop.Loopable;
import webit.script.util.ALU;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
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

    public Object execute(final Context context) {
        final LoopCtrl ctrl = context.loopCtrl;
        final Statement[] statements = this.statements;
        final int preIndex = context.indexer;
        context.indexer = indexer;
        label:
        while (ALU.isTrue(whileExpr.execute(context))) {
            StatementUtil.executeInvertedAndCheckLoops(statements, context);
            if (ctrl.getLoopType() != LoopInfo.NO_LOOP) {
                if (ctrl.matchLabel(label)) {
                    switch (ctrl.getLoopType()) {
                        case LoopInfo.BREAK:
                            ctrl.reset();
                            break label; // while
                        case LoopInfo.RETURN:
                            //can't deal
                            break label; //while
                        case LoopInfo.CONTINUE:
                            ctrl.reset();
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

    public List<LoopInfo> collectPossibleLoopsInfo() {
        return possibleLoopsInfo != null ? new LinkedList<LoopInfo>(Arrays.asList(possibleLoopsInfo)) : null;
    }
}
