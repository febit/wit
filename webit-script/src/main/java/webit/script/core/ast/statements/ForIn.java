// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import webit.script.Context;
import webit.script.core.VariantIndexer;
import webit.script.core.ast.AbstractStatement;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;
import webit.script.core.ast.expressions.FunctionDeclare;
import webit.script.core.ast.loop.LoopCtrl;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.ast.loop.Loopable;
import webit.script.core.runtime.VariantStack;
import webit.script.util.CollectionUtil;
import webit.script.util.StatementUtil;
import webit.script.util.iter.Iter;
import webit.script.util.iter.IterMethodFilter;

/**
 *
 * @author Zqq
 */
public class ForIn extends AbstractStatement implements Loopable {

    protected final FunctionDeclare functionDeclareExpr;
    protected final Expression collectionExpr;
    protected final VariantIndexer varIndexer;
    protected final Statement[] statements;
    protected final LoopInfo[] possibleLoopsInfo;
    protected final Statement elseStatement;
    protected final int label;

    public ForIn(FunctionDeclare functionDeclareExpr, Expression collectionExpr, VariantIndexer varIndexer, Statement[] statements, LoopInfo[] possibleLoopsInfo, Statement elseStatement, int label, int line, int column) {
        super(line, column);
        this.functionDeclareExpr = functionDeclareExpr;
        this.collectionExpr = collectionExpr;
        this.varIndexer = varIndexer;
        this.statements = statements;
        this.possibleLoopsInfo = possibleLoopsInfo;
        this.elseStatement = elseStatement;
        this.label = label;
    }

    public Object execute(final Context context) {
        Iter iter = CollectionUtil.toIter(StatementUtil.execute(collectionExpr, context));
        if (functionDeclareExpr != null) {
            iter = new IterMethodFilter(context,
                    functionDeclareExpr.execute(context),
                    iter);
        }
        if (iter != null
                && iter.hasNext()) {

            final VariantStack vars;
            final LoopCtrl ctrl = context.loopCtrl;
            final Statement[] statements = this.statements;
            (vars = context.vars).push(varIndexer);
            vars.set(0, iter);
            label:
            do {
                vars.resetForForIn(iter.next());
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
            vars.pop();
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
