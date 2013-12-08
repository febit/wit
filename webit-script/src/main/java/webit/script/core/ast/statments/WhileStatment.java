// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statment;
import webit.script.core.ast.loop.LoopCtrl;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.ast.loop.Loopable;
import webit.script.core.VariantIndexer;
import webit.script.core.runtime.VariantStack;
import webit.script.util.ALU;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class WhileStatment extends AbstractStatment implements Loopable {

    private final Expression whileExpr;
    private final VariantIndexer varIndexer;
    private final Statment[] statments;
    public final LoopInfo[] possibleLoopsInfo;
    private final int label;

    public WhileStatment(Expression whileExpr, VariantIndexer varIndexer, Statment[] statments, LoopInfo[] possibleLoopsInfo, int label, int line, int column) {
        super(line, column);
        this.whileExpr = whileExpr;
        this.varIndexer = varIndexer;
        this.statments = statments;
        this.possibleLoopsInfo = possibleLoopsInfo;
        this.label = label;
    }

    public Object execute(final Context context) {
        final LoopCtrl ctrl = context.loopCtrl;
        final Statment[] statments = this.statments;
        final VariantStack vars;
        (vars = context.vars).push(varIndexer);
        label:
        while (ALU.isTrue(StatmentUtil.execute(whileExpr, context))) {
            StatmentUtil.executeInvertedAndCheckLoops(statments, context);
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
            vars.resetCurrent();
        }
        vars.pop();
        return null;
    }

    public List<LoopInfo> collectPossibleLoopsInfo() {
        return possibleLoopsInfo != null ? new LinkedList<LoopInfo>(Arrays.asList(possibleLoopsInfo)) : null;
    }
}
