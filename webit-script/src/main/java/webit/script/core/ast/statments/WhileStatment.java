// Copyright (c) 2013, Webit Team. All Rights Reserved.

package webit.script.core.ast.statments;

import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Expression;
import webit.script.core.runtime.LoopCtrl;
import webit.script.util.ALU;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class WhileStatment extends AbstractStatment {

    private final Expression whileExpr;
    private final BlockStatment bodyStatment;
    private final boolean checkAtFirst;
    private final String label;

    public WhileStatment(Expression whileExpr, BlockStatment bodyStatment, boolean checkAtFirst, String label, int line, int column) {
        super(line, column);
        this.whileExpr = whileExpr;
        this.bodyStatment = bodyStatment;
        this.checkAtFirst = checkAtFirst;
        this.label = label;
    }

    @Override
    public void execute(Context context) {
        boolean go = checkAtFirst ? ALU.toBoolean(StatmentUtil.execute(whileExpr, context)) : true;
        LoopCtrl ctrl = context.loopCtrl;
        label:
        while (go) {
            StatmentUtil.execute(bodyStatment, context);

            if (!ctrl.goon()) {
                if (ctrl.matchLabel(label)) {
                    switch (ctrl.getLoopType()) {
                        case BREAK:
                            ctrl.reset();
                            break label; // while
                        case RETURN:
                            //can't deal
                            break label; //while
                        case CONTINUE:
                            ctrl.reset();
                            break; //switch
                        default:
                            break label; //while
                    }
                } else {
                    break;
                }
            }
            go = ALU.toBoolean(StatmentUtil.execute(whileExpr, context));
        }
    }
}
