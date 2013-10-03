// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.method;

import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Statment;
import webit.script.core.ast.loop.LoopCtrl;
import webit.script.core.ast.loop.LoopType;
import webit.script.core.runtime.variant.VariantMap;
import webit.script.core.runtime.variant.VariantStack;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class Function extends AbstractStatment {

    private final int argsIndex;
    private final int[] argIndexs; //with arguments at first
    public final int[] overflowUpstairs;
    public final int overflowUpstairsRange;
    private final VariantMap varMap;
    private final Statment[] statments;
    private final boolean hasReturnLoops;

    public Function(int argsIndex, int[] argIndexs, int[] overflowUpstairs, VariantMap varMap, Statment[] statments, boolean hasReturnLoops, int line, int column) {
        super(line, column);
        this.argIndexs = argIndexs;
        this.argsIndex = argsIndex;
        this.overflowUpstairs = overflowUpstairs;
        this.overflowUpstairsRange = overflowUpstairs != null ? overflowUpstairs[overflowUpstairs.length - 1] - overflowUpstairs[0] : -1;
        this.varMap = varMap;
        this.statments = statments;
        this.hasReturnLoops = hasReturnLoops;
    }

    public Object execute(final Context context) {
        return execute(context, null);
    }

    public Object execute(final Context context, final Object[] args) {

        final VariantStack vars;
        (vars = context.vars).push(varMap);
        vars.set(0, argsIndex, args);
        vars.set(argIndexs, args);

        if (hasReturnLoops) {

            StatmentUtil.executeAndCheckLoops(statments, context);
            vars.pop();

            final LoopCtrl ctrl;
            if (!(ctrl = context.loopCtrl).goon() && ctrl.getLoopType() == LoopType.RETURN) {
                Object result = ctrl.getLoopValue();
                ctrl.reset();
                return result;
            }
        } else {
            StatmentUtil.execute(statments, context);
            vars.pop();
        }
        return Context.VOID;
    }
}
