// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.statements;

import java.util.List;
import org.febit.wit.InternalContext;
import org.febit.wit.core.LoopInfo;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.core.ast.Loopable;
import org.febit.wit.core.ast.Statement;
import org.febit.wit.core.ast.expressions.FunctionDeclare;
import org.febit.wit.lang.Iter;
import org.febit.wit.lang.iter.IterMethodFilter;
import org.febit.wit.util.CollectionUtil;
import org.febit.wit.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public class ForIn extends Statement implements Loopable {

    protected final FunctionDeclare functionDeclareExpr;
    protected final Expression collectionExpr;
    protected final int indexer;
    protected final Statement[] statements;
    protected final LoopInfo[] possibleLoops;
    protected final Statement elseStatement;
    protected final int label;
    protected final int iterIndex;
    protected final int itemIndex;

    public ForIn(FunctionDeclare functionDeclareExpr, Expression collectionExpr, int indexer, int iterIndex, int itemIndex, Statement[] statements, LoopInfo[] possibleLoops, Statement elseStatement, int label, int line, int column) {
        super(line, column);
        this.functionDeclareExpr = functionDeclareExpr;
        this.collectionExpr = collectionExpr;
        this.indexer = indexer;
        this.statements = statements;
        this.possibleLoops = possibleLoops;
        this.elseStatement = elseStatement;
        this.label = label;
        this.iterIndex = iterIndex;
        this.itemIndex = itemIndex;
    }

    @Override
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
