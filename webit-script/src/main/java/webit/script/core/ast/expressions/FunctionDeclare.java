// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.VariantIndexer;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.Statement;
import webit.script.core.runtime.VariantContext;
import webit.script.core.runtime.VariantStack;
import webit.script.method.impl.FunctionMethodDeclare;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public final class FunctionDeclare extends AbstractExpression {

    private final int argsCount;
    public final int[] _overflowUpstairs;
    private final VariantIndexer varIndexer;
    private final Statement[] statements;
    private final boolean hasReturnLoops;

    public FunctionDeclare(int argsCount, int[] overflowUpstairs, VariantIndexer varIndexer, Statement[] statements, boolean hasReturnLoops, int line, int column) {
        super(line, column);
        this.argsCount = argsCount;
        this._overflowUpstairs = overflowUpstairs != null && overflowUpstairs.length != 0 ? overflowUpstairs : null;
        this.varIndexer = varIndexer;
        this.statements = statements;
        this.hasReturnLoops = hasReturnLoops;
    }

    public Object execute(final Context context) {
        final VariantContext[] variantContexts;
        final int[] overflowUpstairs;
        final boolean containsRootContext;
        if ((overflowUpstairs = this._overflowUpstairs) != null) {
            final int len;
            final int max;
            int j = -1;
            final VariantStack vars = context.vars;
            variantContexts = new VariantContext[(max = overflowUpstairs[(len = overflowUpstairs.length) - 1]) + 1];
            for (int i = 0; i < len; i++) {
                variantContexts[max - (j = overflowUpstairs[i])] = vars.getContext(j);
            }
            containsRootContext = j == vars.getCurrentDepth();
        } else {
            variantContexts = null;
            containsRootContext = false;
        }
        return new FunctionMethodDeclare(this, context.template, variantContexts, containsRootContext);
    }

    public Object invoke(final Context context, final Object[] args) {
        final VariantStack vars;
        (vars = context.vars).push(varIndexer);
        vars.setArgumentsForFunction(argsCount, args);
        if (hasReturnLoops) {
            StatementUtil.executeInvertedAndCheckLoops(statements, context);
            vars.pop();
            return context.loopCtrl.resetReturnLoop();
        } else {
            StatementUtil.executeInverted(statements, context);
            vars.pop();
        }
        return Context.VOID;
    }
}
