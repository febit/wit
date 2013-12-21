// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import webit.script.Context;
import webit.script.core.VariantIndexer;
import webit.script.core.ast.AbstractStatement;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;
import webit.script.core.ast.loop.LoopCtrl;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.ast.loop.Loopable;
import webit.script.core.runtime.VariantStack;
import webit.script.util.CollectionUtil;
import webit.script.util.StatementUtil;
import webit.script.util.collection.Iter;

/**
 *
 * @author Zqq
 */
public final class ForIn extends AbstractStatement implements Loopable {

    private final Expression collectionExpr;
    private final VariantIndexer varIndexer;
    private final Statement[] statements;
    public final LoopInfo[] possibleLoopsInfo;
    private final Statement elseStatement;
    private final int label;

    public ForIn(Expression collectionExpr, VariantIndexer varIndexer, Statement[] statements, LoopInfo[] possibleLoopsInfo, Statement elseStatement, int label, int line, int column) {
        super(line, column);
        this.collectionExpr = collectionExpr;
        this.varIndexer = varIndexer;
        this.statements = statements;
        this.possibleLoopsInfo = possibleLoopsInfo;
        this.elseStatement = elseStatement;
        this.label = label;
    }

    public Object execute(final Context context) {
        final Iter iter;
        if ((iter = CollectionUtil.toIter(
                StatementUtil.execute(collectionExpr, context))) != null
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
