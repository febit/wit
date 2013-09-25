// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.method;

import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Statment;
import webit.script.core.runtime.LoopCtrl;
import webit.script.core.runtime.variant.VariantStack;
import webit.script.core.runtime.variant.VariantMap;
import webit.script.exceptions.ScriptRuntimeException;
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

    public Function(int argsIndex, int[] argIndexs, int[] overflowUpstairs, VariantMap varMap, Statment[] statments, int line, int column) {
        super(line, column);
        this.argIndexs = argIndexs;
        this.argsIndex = argsIndex;
        overflowUpstairs = overflowUpstairs != null && overflowUpstairs.length > 0 ? overflowUpstairs : null;
        this.overflowUpstairs = overflowUpstairs != null ? overflowUpstairs : null;
        this.overflowUpstairsRange = overflowUpstairs != null ? overflowUpstairs[overflowUpstairs.length - 1] - overflowUpstairs[0] : -1;
        this.varMap = varMap;
        this.statments = statments;
    }

    public Object execute(final Context context) {
        return execute(context, null);
    }

    public Object execute(final Context context, final Object[] args) {

        final VariantStack vars = context.vars;
        vars.push(varMap);
        vars.set(0, argsIndex, args);
        vars.set(argIndexs, args);
        final int len = statments.length;
        final LoopCtrl ctrl = context.loopCtrl;
        for (int i = 0; i < len && ctrl.goon(); i++) {
            StatmentUtil.execute(statments[i], context);
        }
        vars.pop();

        if (!ctrl.goon()) {
            switch (ctrl.getLoopType()) {
                case BREAK:
                    throw new ScriptRuntimeException("break loop overflow");
                case RETURN:
                    return ctrl.getLoopValue(); //return
                case CONTINUE:
                    throw new ScriptRuntimeException("continue loop overflow");
                default:
                    break;
            }
        }
        return Context.VOID;
    }
}
