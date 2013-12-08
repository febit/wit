// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statment;
import webit.script.core.ast.loop.LoopCtrl;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.ast.loop.Loopable;
import webit.script.core.VariantIndexer;
import webit.script.core.runtime.VariantStack;
import webit.script.util.CollectionUtil;
import webit.script.util.StatmentUtil;
import webit.script.util.collection.Iter;

/**
 *
 * @author Zqq
 */
public final class ForInStatment extends AbstractStatment implements Loopable {

    private final int iterIndex;
    private final int itemIndex;
    private final Expression collectionExpr;
    private final VariantIndexer varIndexer;
    private final Statment[] statments;
    public final LoopInfo[] possibleLoopsInfo;
    private final Statment elseStatment;
    private final int label;

    public ForInStatment(int iterIndex, int itemIndex, Expression collectionExpr, VariantIndexer varIndexer, Statment[] statments, LoopInfo[] possibleLoopsInfo, Statment elseStatment, int label, int line, int column) {
        super(line, column);
        this.iterIndex = iterIndex;
        this.itemIndex = itemIndex;
        this.collectionExpr = collectionExpr;
        this.varIndexer = varIndexer;
        this.statments = statments;
        this.possibleLoopsInfo = possibleLoopsInfo;
        this.elseStatment = elseStatment;
        this.label = label;
    }

    public Object execute(final Context context) {
        final Iter iter;
        if ((iter = CollectionUtil.toIter(
                StatmentUtil.execute(collectionExpr, context))) != null
                && iter.hasNext()) {

            final VariantStack vars;
            final LoopCtrl ctrl = context.loopCtrl;
            final Statment[] statments = this.statments;
            (vars = context.vars).push(varIndexer);
            label:
            do {
                vars.resetCurrentWith(iterIndex, iter, itemIndex, iter.next());
                StatmentUtil.executeInvertedAndCheckLoops(statments, context);
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
        } else if (elseStatment != null) {
            StatmentUtil.execute(elseStatment, context);
        }
        return null;
    }

    public List<LoopInfo> collectPossibleLoopsInfo() {
        return possibleLoopsInfo != null ? new LinkedList<LoopInfo>(Arrays.asList(possibleLoopsInfo)) : null;
    }
}
