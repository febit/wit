// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.Iter;
import org.febit.wit.lang.LoopMeta;
import org.febit.wit.lang.Loopable;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.Statement;
import org.febit.wit.lang.ast.expr.FunctionDeclare;
import org.febit.wit.lang.iter.IterMethodFilter;
import org.febit.wit.util.CollectionUtil;
import org.febit.wit.util.StatementUtil;

import java.util.List;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public class ForIn implements Statement, Loopable {

    protected final FunctionDeclare functionDeclareExpr;
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
    @SuppressWarnings({
            "squid:S3776" // Cognitive Complexity of methods should not be too high
    })
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
            final int myLabel = this.label;
            final int index = this.itemIndex;
            final Object[] vars = context.vars;
            vars[iterIndex] = iter;
            label:
            do {
                vars[index] = iter.next();
                context.executeWithLoop(stats);
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
            context.indexer = preIndex;
            return null;
        } else if (elseStatement != null) {
            elseStatement.execute(context);
        }
        return null;
    }

    @Override
    public List<LoopMeta> collectPossibleLoops() {
        return StatementUtil.asList(possibleLoops);
    }
}
