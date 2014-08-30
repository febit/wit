// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;
import webit.script.lang.method.FunctionMethodDeclare;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public final class FunctionDeclare extends Expression {

    private final int argsCount;
    private final int indexer;
    private final Statement[] statements;
    private final boolean hasReturnLoops;
    private final int start;

    public FunctionDeclare(int argsCount, int indexer, Statement[] statements, int start, boolean hasReturnLoops, int line, int column) {
        super(line, column);
        this.argsCount = argsCount;
        this.indexer = indexer;
        this.statements = statements;
        this.hasReturnLoops = hasReturnLoops;
        this.start = start;
    }

    public FunctionMethodDeclare execute(final Context context) {
        return new FunctionMethodDeclare(this, context.template, context.vars, context.indexers);
    }

    public Object invoke(final Context context, final Object[] args) {
        final int preIndex = context.indexer;
        context.indexer = indexer;
        final int argsCount = this.argsCount;
        final Object[] vars = context.vars;
        int start = this.start;
        vars[start++] = args;
        int len = args != null ? args.length : 0;
        if (argsCount != 0) {
            int nextEnd = argsCount > len ? len : argsCount;
            int i;
            for (i = 0; i < nextEnd; i++) {
                vars[start++] = args[i];
            }
            if (argsCount > len) {
                nextEnd = argsCount - len;
                for (i = 0; i < nextEnd; i++) {
                    vars[start++] = null;
                }
            }
        }
        if (hasReturnLoops) {
            StatementUtil.executeInvertedAndCheckLoops(statements, context);
            context.indexer = preIndex;
            return context.loopCtrl.resetReturnLoop();
        } else {
            StatementUtil.executeInverted(statements, context);
            context.indexer = preIndex;
            return Context.VOID;
        }
    }
}
