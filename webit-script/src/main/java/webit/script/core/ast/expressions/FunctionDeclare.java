// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.VariantIndexer;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;
import webit.script.lang.method.FunctionMethodDeclare;
import webit.script.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public final class FunctionDeclare extends Expression {

    private final int argsCount;
    private final VariantIndexer[] indexers;
    private final Statement[] statements;
    private final boolean hasReturnLoops;
    private final int start;
    private final int varSize;

    public FunctionDeclare(int argsCount,int varSize, VariantIndexer[] indexers, Statement[] statements, int start, boolean hasReturnLoops, int line, int column) {
        super(line, column);
        this.argsCount = argsCount;
        this.indexers = indexers;
        this.statements = statements;
        this.hasReturnLoops = hasReturnLoops;
        this.start = start;
        this.varSize = varSize;
    }

    @Override
    public FunctionMethodDeclare execute(final Context context) {
        return new FunctionMethodDeclare(this, context, indexers, this.varSize);
    }

    public Object invoke(final Context context, final Object[] args) {
        final int argsTotalCount = this.argsCount;
        final Object[] vars = context.vars;
        int argsStart = this.start;
        final int len = args != null ? args.length : 0;
        vars[argsStart++] = args;
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
            return context.resetReturnLoop();
        } else {
            StatementUtil.executeInverted(statements, context);
            return Context.VOID;
        }
    }
}
