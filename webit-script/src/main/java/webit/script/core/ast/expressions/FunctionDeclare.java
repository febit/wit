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
        final int argsTotalCount = this.argsCount;
        final Object[] vars = context.vars;
        int argsStart = this.start;
        vars[argsStart++] = args;
        int len = args != null ? args.length : 0;
        if (argsTotalCount != 0) {
            int nextEnd = argsTotalCount > len ? len : argsTotalCount;
            int i;
            for (i = 0; i < nextEnd; i++) {
                vars[argsStart++] = args[i];
            }
            if (argsTotalCount > len) {
                nextEnd = argsTotalCount - len;
                for (i = 0; i < nextEnd; i++) {
                    vars[argsStart++] = null;
                }
            }
        }
        if (hasReturnLoops) {
            StatementUtil.executeInvertedAndCheckLoops(statements, context);
            context.indexer = preIndex;
            return context.resetReturnLoop();
        } else {
            StatementUtil.executeInverted(statements, context);
            context.indexer = preIndex;
            return Context.VOID;
        }
    }
}
