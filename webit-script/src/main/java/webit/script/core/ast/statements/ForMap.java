// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import webit.script.Context;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;
import webit.script.core.ast.expressions.FunctionDeclare;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.ast.loop.Loopable;
import webit.script.lang.KeyIter;
import webit.script.lang.iter.KeyIterMethodFilter;
import webit.script.util.CollectionUtil;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public final class ForMap extends Statement implements Loopable {

    protected final FunctionDeclare functionDeclareExpr;
    private final Expression mapExpr;
    private final int indexer;
    private final Statement[] statements;
    public final LoopInfo[] possibleLoopsInfo;
    private final Statement elseStatement;
    private final int label;
    private final int iterIndex;
    private final int keyIndex;
    private final int valueIndex;

    public ForMap(FunctionDeclare functionDeclareExpr, Expression mapExpr, int indexer, int iterIndex, int keyIndex, int valueIndex, Statement[] statements, LoopInfo[] possibleLoopsInfo, Statement elseStatement, int label, int line, int column) {
        super(line, column);
        this.functionDeclareExpr = functionDeclareExpr;
        this.mapExpr = mapExpr;
        this.indexer = indexer;
        this.statements = statements;
        this.possibleLoopsInfo = possibleLoopsInfo;
        this.elseStatement = elseStatement;
        this.label = label;
        this.iterIndex = iterIndex;
        this.keyIndex = keyIndex;
        this.valueIndex = valueIndex;
    }

    @SuppressWarnings("unchecked")
    public Object execute(final Context context) {
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
                StatementUtil.executeInvertedAndCheckLoops(stats, context);
                if (context.hasLoop()) {
                    if (context.matchLabel(myLabel)) {
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
                    } else {
                        break; //while
                    }
                }
            } while (iter.hasNext());
            context.indexer = preIndex;
            return null;
        } else if (elseStatement != null) {
            elseStatement.execute(context);
        }
        return null;
    }

    public List<LoopInfo> collectPossibleLoopsInfo() {
        return possibleLoopsInfo != null ? new LinkedList<LoopInfo>(Arrays.asList(possibleLoopsInfo)) : null;
    }
}
