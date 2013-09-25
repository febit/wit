// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import java.util.Iterator;
import java.util.List;
import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Expression;
import webit.script.core.ast.loop.LoopCtrl;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.ast.loop.LoopType;
import webit.script.core.ast.loop.Loopable;
import webit.script.util.ALU;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class WhileStatment extends AbstractStatment implements Loopable {

    private final Expression whileExpr;
    private final BlockStatment bodyStatment;
    private final boolean doWhileAtFirst;
    private final String label;

    public WhileStatment(Expression whileExpr, BlockStatment bodyStatment, boolean doWhileAtFirst, String label, int line, int column) {
        super(line, column);
        this.whileExpr = whileExpr;
        this.bodyStatment = bodyStatment;
        this.doWhileAtFirst = doWhileAtFirst;
        this.label = label;
    }

    public Object execute(final Context context) {
        boolean go = doWhileAtFirst ? ALU.toBoolean(StatmentUtil.execute(whileExpr, context)) : true;
        final LoopCtrl ctrl = context.loopCtrl;
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
        return null;
    }

    public List<LoopInfo> collectPossibleLoopsInfo() {
        if (bodyStatment == null) {
            return null;
        }
        List<LoopInfo> loopInfos = bodyStatment.collectPossibleLoopsInfo();
        if (loopInfos == null) {
            return null;
        }
        
        for (Iterator<LoopInfo> it = loopInfos.iterator(); it.hasNext();) {
            LoopInfo loopInfo = it.next();
            if (loopInfo.matchLabel(this.label)
                    &&(
                    loopInfo.type == LoopType.BREAK
                    || loopInfo.type == LoopType.CONTINUE
                    )) {
                it.remove();
            }
        }
        return loopInfos.isEmpty()? null : loopInfos;
    }
    
}
