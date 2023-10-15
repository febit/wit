// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.AstUtils;
import org.febit.wit.lang.KeyIter;
import org.febit.wit.lang.LoopMeta;
import org.febit.wit.lang.Loopable;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.Statement;
import org.febit.wit.lang.ast.expr.FunctionDeclare;
import org.febit.wit.lang.iter.KeyIterMethodFilter;
import org.febit.wit.util.CollectionUtil;

import java.util.List;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public final class ForMap implements Statement, Loopable {

    private final FunctionDeclare functionDeclareExpr;
    private final Expression mapExpr;
    private final int indexer;
    private final int iterIndex;
    private final int keyIndex;
    private final int valueIndex;
    private final Statement[] statements;
    private final LoopMeta[] possibleLoops;
    private final Statement elseStatement;
    private final int label;
    @Getter
    private final Position position;

    @Override
    @SuppressWarnings({
            "squid:S3776" // Cognitive Complexity of methods should not be too high
    })
    public Object execute(final InternalContext context) {
        KeyIter iter = CollectionUtil.toKeyIter(mapExpr.execute(context), this);
        if (iter != null && functionDeclareExpr != null) {
            iter = new KeyIterMethodFilter(context, functionDeclareExpr.execute(context), iter);
        }
        if (iter != null && iter.hasNext()) {
            final int preIndex = context.indexer;
            context.indexer = indexer;
            final Statement[] stats = this.statements;
            final int myLabel = this.label;
            final int indexOfKey = this.keyIndex;
            final int indexOfValue = this.valueIndex;
            final Object[] vars = context.vars;
            vars[iterIndex] = iter;
            label:
            do {
                vars[indexOfKey] = iter.next();
                vars[indexOfValue] = iter.value();
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
        return AstUtils.asList(possibleLoops);
    }
}
