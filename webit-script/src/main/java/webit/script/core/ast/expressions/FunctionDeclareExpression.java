// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.Statment;
import webit.script.core.ast.method.FunctionMethodDeclare;
import webit.script.core.runtime.variant.VariantContext;
import webit.script.core.runtime.variant.VariantMap;
import webit.script.core.runtime.variant.VariantStack;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class FunctionDeclareExpression extends AbstractExpression {

    private final int argsIndex;
    private final int[] argIndexs;
    public final int[] _overflowUpstairs;
    private final VariantMap varMap;
    private final Statment[] statments;
    private final boolean hasReturnLoops;

    public FunctionDeclareExpression(int argsIndex, int[] argIndexs, int[] overflowUpstairs, VariantMap varMap, Statment[] statments, boolean hasReturnLoops, int line, int column) {
        super(line, column);
        this.argIndexs = argIndexs;
        this.argsIndex = argsIndex;
        this._overflowUpstairs = overflowUpstairs != null && overflowUpstairs.length != 0 ? overflowUpstairs : null;
        this.varMap = varMap;
        this.statments = statments;
        this.hasReturnLoops = hasReturnLoops;
    }

    public Object execute(final Context context) {
        final VariantContext[] variantContexts;
        final int[] overflowUpstairs;
        if ((overflowUpstairs = this._overflowUpstairs) != null) {
            final int len;
            final int max;
            variantContexts = new VariantContext[(max = overflowUpstairs[(len = overflowUpstairs.length) - 1]) + 1];
            for (int i = 0, j; i < len; i++) {
                variantContexts[max - (j = overflowUpstairs[i])] = context.vars.getContext(j);
            }
        } else {
            variantContexts = null;
        }
        return new FunctionMethodDeclare(this, context.template, variantContexts);
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
