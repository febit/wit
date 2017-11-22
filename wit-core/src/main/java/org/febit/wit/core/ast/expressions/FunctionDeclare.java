// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.expressions;

import org.febit.wit.InternalContext;
import org.febit.wit.core.VariantIndexer;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.core.ast.Statement;
import org.febit.wit.lang.InternalVoid;
import org.febit.wit.lang.method.FunctionMethodDeclare;
import org.febit.wit.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public final class FunctionDeclare extends Expression {

    private final Object[] argDefaults;
    private final VariantIndexer[] indexers;
    private final Statement[] statements;
    private final boolean hasReturnLoops;
    private final int start;
    private final int varSize;

    public FunctionDeclare(Object[] argDefaults, int varSize, VariantIndexer[] indexers, Statement[] statements, int start, boolean hasReturnLoops, int line, int column) {
        super(line, column);
        this.argDefaults = argDefaults;
        this.indexers = indexers;
        this.statements = statements;
        this.hasReturnLoops = hasReturnLoops;
        this.start = start;
        this.varSize = varSize;
    }

    @Override
    public FunctionMethodDeclare execute(final InternalContext context) {
        return new FunctionMethodDeclare(this, context, indexers, this.varSize);
    }

    public Object invoke(final InternalContext context, final Object[] args) {
        final Object[] defaults = this.argDefaults;
        final int argsTotalCount = defaults.length;
        final Object[] vars = context.vars;
        int argsStart = this.start;
        final int len = args != null ? args.length : 0;
        vars[argsStart++] = args;
        if (argsTotalCount != 0) {
            int passedLen = argsTotalCount > len ? len : argsTotalCount;
            int i = 0;
            for (; i < passedLen; i++) {
                Object arg = args[i];
                vars[argsStart++] = arg != null ? arg : defaults[i];
            }
            for (; i < argsTotalCount; i++) {
                vars[argsStart++] = defaults[i];
            }
        }
        if (hasReturnLoops) {
            StatementUtil.executeWithLoopCheck(statements, context);
            return context.resetReturnLoop();
        } else {
            StatementUtil.execute(statements, context);
            return InternalVoid.VOID;
        }
    }
}
