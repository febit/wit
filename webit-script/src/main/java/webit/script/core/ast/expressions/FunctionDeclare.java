// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.Variants;
import webit.script.core.VariantIndexer;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.Statement;
import webit.script.lang.method.FunctionMethodDeclare;
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

    public FunctionMethodDeclare execute(final Context context) {
        final Variants[] varses;
        final int[] overflowUpstairs;
        final boolean containsRootContext;
        if ((overflowUpstairs = this._overflowUpstairs) != null) {
            final int len;
            final int max;
            int j = -1;
            varses = new Variants[(max = overflowUpstairs[(len = overflowUpstairs.length) - 1]) + 1];
            for (int i = 0; i < len; i++) {
                varses[max - (j = overflowUpstairs[i])] = context.getVars(j);
            }
            containsRootContext = j == context.getCurrentVarsDepth();
        } else {
            varses = null;
            containsRootContext = false;
        }
        return new FunctionMethodDeclare(this, context.template, varses, containsRootContext);
    }

    public Object invoke(final Context context, final Object[] args) {
        context.push(varIndexer);
        context.setArgumentsForFunction(argsCount, args);
        if (hasReturnLoops) {
            StatementUtil.executeInvertedAndCheckLoops(statements, context);
            context.pop();
            return context.loopCtrl.resetReturnLoop();
        } else {
            StatementUtil.executeInverted(statements, context);
            context.pop();
            return Context.VOID;
        }
    }
}
