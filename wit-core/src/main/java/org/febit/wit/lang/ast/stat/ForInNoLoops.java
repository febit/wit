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
import org.febit.wit.lang.ast.expr.FunctionDeclareExpr;
import org.febit.wit.lang.iter.IterMethodFilter;
import org.febit.wit.util.CollectionUtil;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public final class ForInNoLoops implements Statement {

    private final FunctionDeclareExpr filterFuncDeclare;
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
        Iter iter = iter(context);
        if (iter.hasNext()) {
            return context.pushIndexer(indexer, c -> this.execute0(c, iter));
        }
        if (elseStatement != null) {
            elseStatement.execute(context);
        }
        return null;
    }

    private Iter iter(InternalContext context) {
        var iter = CollectionUtil.toIter(collectionExpr.execute(context), this);
        if (filterFuncDeclare == null) {
            return iter;
        }
        return new IterMethodFilter(context, filterFuncDeclare.execute(context), iter);
    }

    @Nullable
    @SuppressWarnings({
            "UnnecessaryLocalVariable",
            "squid:S3776", // Cognitive Complexity of methods should not be too high
    })
    private Object execute0(InternalContext context, Iter iter) {
        var stats = this.statements;
        var itemIdx = this.itemIndex;
        var vars = context.vars;
        vars[iterIndex] = iter;
        do {
            vars[itemIdx] = iter.next();
            context.visit(stats);
        } while (iter.hasNext());
        return null;
    }
}
