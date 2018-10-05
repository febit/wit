// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.statements;

import org.febit.wit.InternalContext;
import org.febit.wit.core.LoopInfo;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.core.ast.Loopable;
import org.febit.wit.core.ast.Statement;
import org.febit.wit.core.ast.expressions.FunctionDeclare;
import org.febit.wit.lang.KeyIter;
import org.febit.wit.lang.iter.KeyIterMethodFilter;
import org.febit.wit.util.CollectionUtil;
import org.febit.wit.util.StatementUtil;

import java.util.List;

/**
 * @author zqq90
 */
public final class ForMap extends Statement implements Loopable {

    private final FunctionDeclare functionDeclareExpr;
    private final Expression mapExpr;
    private final int indexer;
    private final Statement[] statements;
    private final LoopInfo[] possibleLoops;
    private final Statement elseStatement;
    private final int label;
    private final int iterIndex;
    private final int keyIndex;
    private final int valueIndex;

    public ForMap(FunctionDeclare functionDeclareExpr, Expression mapExpr, int indexer, int iterIndex,
                  int keyIndex, int valueIndex, Statement[] statements, LoopInfo[] possibleLoops,
                  Statement elseStatement, int label, int line, int column) {
        super(line, column);
        this.functionDeclareExpr = functionDeclareExpr;
        this.mapExpr = mapExpr;
        this.indexer = indexer;
        this.statements = statements;
        this.possibleLoops = possibleLoops;
        this.elseStatement = elseStatement;
        this.label = label;
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
            final int myLabel = this.label;
            final int indexOfKey = this.keyIndex;
            final int indexOfValue = this.valueIndex;
            final Object[] vars = context.vars;
            vars[iterIndex] = iter;
            label:
            do {
                vars[indexOfKey] = iter.next();
                vars[indexOfValue] = iter.value();
                StatementUtil.executeWithLoopCheck(stats, context);
                if (context.noLoop()) {
                    continue;
                }
                if (!context.matchLabel(myLabel)) {
                    break; //while
                }
                switch (context.getLoopType()) {
                    case LoopInfo.BREAK:
                        context.resetLoop();
                        break label; // while
                    case LoopInfo.RETURN:
                        //can't deal
                        break label; //while
                    case LoopInfo.CONTINUE:
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
    public List<LoopInfo> collectPossibleLoops() {
        return StatementUtil.asList(possibleLoops);
    }
}
