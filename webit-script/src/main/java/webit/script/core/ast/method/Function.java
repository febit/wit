// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.method;

import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Statment;
import webit.script.core.runtime.variant.VariantMap;
import webit.script.core.runtime.variant.VariantStack;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class Function extends AbstractStatment {

    private final int argsIndex;
    private final int[] argIndexs;
    public final int[] overflowUpstairs;
    private final VariantMap varMap;
    private final Statment[] statments;
    private final boolean hasReturnLoops;

    public Function(int argsIndex, int[] argIndexs, int[] overflowUpstairs, VariantMap varMap, Statment[] statments, boolean hasReturnLoops, int line, int column) {
        super(line, column);
        this.argIndexs = argIndexs;
        this.argsIndex = argsIndex;
        this.overflowUpstairs = overflowUpstairs != null && overflowUpstairs.length != 0 ? overflowUpstairs : null;
        this.varMap = varMap;
        this.statments = statments;
        this.hasReturnLoops = hasReturnLoops;
    }

    public Object execute(final Context context) {
        //Note: not support
        return null;
    }

    public Object invoke(final Context context, final Object[] args) {

        final VariantStack vars;
        (vars = context.vars).push(varMap);

        vars.setArgumentsForFunction(argsIndex, argIndexs, args);

        if (hasReturnLoops) {

            StatmentUtil.executeInvertedAndCheckLoops(statments, context);
            vars.pop();

            return context.loopCtrl.resetReturnLoop();
        } else {
            StatmentUtil.executeInverted(statments, context);
            vars.pop();
        }
        return Context.VOID;
    }
}
