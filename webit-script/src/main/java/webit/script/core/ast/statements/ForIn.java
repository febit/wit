// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import webit.script.Context;
import webit.script.core.VariantIndexer;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;
import webit.script.core.ast.expressions.FunctionDeclare;
import webit.script.core.ast.loop.LoopCtrl;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.ast.loop.Loopable;
import webit.script.lang.Iter;
import webit.script.lang.iter.IterMethodFilter;
import webit.script.util.CollectionUtil;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public class ForIn extends Statement implements Loopable {

    protected final FunctionDeclare functionDeclareExpr;
    protected final Expression collectionExpr;
    protected final int indexer;
    protected final Statement[] statements;
    protected final LoopInfo[] possibleLoopsInfo;
    protected final Statement elseStatement;
    protected final int label;
    protected final int iterIndex;
    protected final int itemIndex;

    public ForIn(FunctionDeclare functionDeclareExpr, Expression collectionExpr, int indexer, int iterIndex, int itemIndex, Statement[] statements, LoopInfo[] possibleLoopsInfo, Statement elseStatement, int label, int line, int column) {
        super(line, column);
        this.functionDeclareExpr = functionDeclareExpr;
        this.collectionExpr = collectionExpr;
        this.indexer = indexer;
        this.statements = statements;
        this.possibleLoopsInfo = possibleLoopsInfo;
        this.elseStatement = elseStatement;
        this.label = label;
        this.iterIndex = iterIndex;
        this.itemIndex = itemIndex;
    }

    public Object execute(final Context context) {
        Iter iter = CollectionUtil.toIter(StatementUtil.execute(collectionExpr, context));
        if (iter != null && functionDeclareExpr != null) {
            iter = new IterMethodFilter(context, functionDeclareExpr.execute(context), iter);
        }
        if (iter != null
                && iter.hasNext()) {
            final LoopCtrl ctrl = context.loopCtrl;
        final int preIndex = context.indexer;
        context.indexer = indexer;
            final Statement[] statements = this.statements;
            final int label = this.label;
            final int itemIndex = this.itemIndex;
            final Object[] vars = context.vars;
            vars[iterIndex] = iter;
            label:
            do {
                vars[itemIndex] = iter.next();
                StatementUtil.executeInvertedAndCheckLoops(statements, context);
                if (ctrl.getLoopType() != LoopInfo.NO_LOOP) {
                    if (ctrl.matchLabel(label)) {
                        switch (ctrl.getLoopType()) {
                            case LoopInfo.BREAK:
                                ctrl.reset();
                                break label; // while
                            case LoopInfo.RETURN:
                                //can't deal
                                break label; //while
                            case LoopInfo.CONTINUE:
                                ctrl.reset();
                                break; //switch
                            default:
                                break label; //while
                            }
                    } else {
                        break;
                    }
                }
            } while (iter.hasNext());
            context.indexer = preIndex;
            return null;
        } else if (elseStatement != null) {
            StatementUtil.execute(elseStatement, context);
        }
        return null;
    }

    public List<LoopInfo> collectPossibleLoopsInfo() {
        return possibleLoopsInfo != null ? new LinkedList<LoopInfo>(Arrays.asList(possibleLoopsInfo)) : null;
    }
}
