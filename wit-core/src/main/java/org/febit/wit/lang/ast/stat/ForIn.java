// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.AstUtils;
import org.febit.wit.lang.Iter;
import org.febit.wit.lang.LoopMeta;
import org.febit.wit.lang.Loopable;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.Statement;
import org.febit.wit.lang.ast.expr.FunctionDeclareExpr;
import org.febit.wit.lang.iter.IterMethodFilter;
import org.febit.wit.util.CollectionUtil;

import java.util.List;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public class ForIn implements Statement, Loopable {

    protected final FunctionDeclareExpr functionDeclareExpr;
    protected final Expression collectionExpr;
    protected final int indexer;
    protected final int iterIndex;
    protected final int itemIndex;
    protected final Statement[] statements;
    protected final LoopMeta[] possibleLoops;
    protected final Statement elseStatement;
    protected final int label;
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
        if (functionDeclareExpr == null) {
            return iter;
        }
        return new IterMethodFilter(context, functionDeclareExpr.execute(context), iter);
    }

    @Nullable
    @SuppressWarnings({
            "UnnecessaryLocalVariable",
            "squid:S3776", // Cognitive Complexity of methods should not be too high
    })
    private Object execute0(InternalContext context, Iter iter) {
        var stats = this.statements;
        var myLabel = this.label;
        var itemIdx = this.itemIndex;
        var vars = context.vars;
        vars[iterIndex] = iter;
        label:
        do {
            vars[itemIdx] = iter.next();
            context.visitAndCheckLoop(stats);
            if (context.noLoop()) {
                continue;
            }
            if (!context.matchLabel(myLabel)) {
                break; //while
            }
            switch (context.getLoopType()) {
                case LoopMeta.BREAK:
                    context.resetLoop();
                    break label; // while
                case LoopMeta.RETURN:
                    //can't deal
                    break label; //while
                case LoopMeta.CONTINUE:
                    context.resetLoop();
                    break; //switch
                default:
                    break label; //while
            }
        } while (iter.hasNext());
        return null;
    }

    @Override
    public List<LoopMeta> collectPossibleLoops() {
        return AstUtils.asList(possibleLoops);
    }
}
