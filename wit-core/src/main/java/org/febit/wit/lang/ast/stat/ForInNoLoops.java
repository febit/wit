// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.Iter;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.Statement;
import org.febit.wit.lang.ast.expr.FunctionDeclare;
import org.febit.wit.lang.iter.IterMethodFilter;
import org.febit.wit.util.CollectionUtil;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public final class ForInNoLoops implements Statement {

    private final FunctionDeclare functionDeclareExpr;
    private final Expression collectionExpr;
    private final int indexer;
    private final int iterIndex;
    private final int itemIndex;
    private final Statement[] statements;
    private final Statement elseStatement;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(final InternalContext context) {
        Iter iter = CollectionUtil.toIter(collectionExpr.execute(context), this);
        if (iter != null && functionDeclareExpr != null) {
            iter = new IterMethodFilter(context, functionDeclareExpr.execute(context), iter);
        }
        if (iter != null
                && iter.hasNext()) {
            final int preIndex = context.indexer;
            context.indexer = indexer;
            final Statement[] stats = this.statements;
            final int i = this.itemIndex;
            final Object[] vars = context.vars;
            vars[iterIndex] = iter;
            do {
                vars[i] = iter.next();
                context.execute(stats);
            } while (iter.hasNext());
            context.indexer = preIndex;
            return null;
        } else if (elseStatement != null) {
            elseStatement.execute(context);
        }
        return null;
    }
}
