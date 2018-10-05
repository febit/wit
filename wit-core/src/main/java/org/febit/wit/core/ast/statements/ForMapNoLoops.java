// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.statements;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.core.ast.Statement;
import org.febit.wit.core.ast.expressions.FunctionDeclare;
import org.febit.wit.lang.KeyIter;
import org.febit.wit.lang.iter.KeyIterMethodFilter;
import org.febit.wit.util.CollectionUtil;
import org.febit.wit.util.StatementUtil;

/**
 * @author zqq90
 */
public final class ForMapNoLoops extends Statement {

    private final FunctionDeclare functionDeclareExpr;
    private final Expression mapExpr;
    private final int indexer;
    private final Statement[] statements;
    private final Statement elseStatement;
    private final int iterIndex;
    private final int keyIndex;
    private final int valueIndex;

    public ForMapNoLoops(FunctionDeclare functionDeclareExpr, Expression mapExpr, int indexer,
                         int iterIndex, int keyIndex, int valueIndex, Statement[] statements,
                         Statement elseStatement, int line, int column) {
        super(line, column);
        this.functionDeclareExpr = functionDeclareExpr;
        this.mapExpr = mapExpr;
        this.indexer = indexer;
        this.statements = statements;
        this.elseStatement = elseStatement;
        this.iterIndex = iterIndex;
        this.keyIndex = keyIndex;
        this.valueIndex = valueIndex;
    }

    @Override
    public Object execute(final InternalContext context) {
        KeyIter iter = CollectionUtil.toKeyIter(mapExpr.execute(context), this);
        if (iter != null && functionDeclareExpr != null) {
            iter = new KeyIterMethodFilter(context, functionDeclareExpr.execute(context), iter);
        }
        if (iter != null && iter.hasNext()) {
            final int preIndex = context.indexer;
            context.indexer = indexer;
            final Statement[] stats = this.statements;
            final int indexOfKey = this.keyIndex;
            final int indexOfValue = this.valueIndex;
            final Object[] vars = context.vars;
            vars[iterIndex] = iter;
            do {
                vars[indexOfKey] = iter.next();
                vars[indexOfValue] = iter.value();
                StatementUtil.execute(stats, context);
            } while (iter.hasNext());
            context.indexer = preIndex;
            return null;
        } else if (elseStatement != null) {
            elseStatement.execute(context);
        }
        return null;
    }
}
