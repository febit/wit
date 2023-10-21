// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import jakarta.annotation.Nullable;
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
import org.febit.wit.lang.ast.expr.FunctionDeclareExpr;
import org.febit.wit.lang.iter.KeyIterMethodFilter;
import org.febit.wit.util.CollectionUtil;

import java.util.List;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public final class ForMap implements Statement, Loopable {

    private final FunctionDeclareExpr filterFuncDeclare;
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
    @Nullable
    public Object execute(final InternalContext context) {
        var iter = iter(context);
        if (iter.hasNext()) {
            return context.pushIndexer(indexer, c -> this.execute0(c, iter));
        }
        if (elseStatement != null) {
            elseStatement.execute(context);
        }
        return null;
    }

    private KeyIter iter(InternalContext context) {
        var iter = CollectionUtil.toKeyIter(mapExpr.execute(context), this);
        if (filterFuncDeclare == null) {
            return iter;
        }
        return new KeyIterMethodFilter(context, filterFuncDeclare.execute(context), iter);
    }

    @Nullable
    @SuppressWarnings({
            "UnnecessaryLocalVariable",
            "squid:S3776", // Cognitive Complexity of methods should not be too high
    })
    private Object execute0(InternalContext context, KeyIter iter) {
        var stats = this.statements;
        var myLabel = this.label;
        var keyIdx = this.keyIndex;
        var valIdx = this.valueIndex;
        var vars = context.vars;
        vars[iterIndex] = iter;
        label:
        do {
            vars[keyIdx] = iter.next();
            vars[valIdx] = iter.value();
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
