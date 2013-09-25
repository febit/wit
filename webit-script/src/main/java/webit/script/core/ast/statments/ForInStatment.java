// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import java.util.Iterator;
import java.util.List;
import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Optimizable;
import webit.script.core.ast.Statment;
import webit.script.core.ast.loop.LoopCtrl;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.ast.loop.LoopType;
import webit.script.core.ast.loop.Loopable;
import webit.script.util.CollectionUtil;
import webit.script.util.StatmentUtil;
import webit.script.util.collection.Iter;

/**
 *
 * @author Zqq
 */
public final class ForInStatment extends AbstractStatment implements Optimizable, Loopable {

    private final int[] paramIndexs;
    private final Expression collectionExpr;
    private final BlockStatment bodyStatment;
    private final Statment elseStatment;
    private final String label;

    public ForInStatment(int itemIndex, int iterIndex, Expression collectionExpr, BlockStatment bodyStatment, Statment elseStatment, String label, int line, int column) {
        super(line, column);
        this.paramIndexs = new int[]{itemIndex, iterIndex};
        this.collectionExpr = collectionExpr;
        this.bodyStatment = bodyStatment;
        this.elseStatment = elseStatment;
        this.label = label;
    }

    public Object execute(final Context context) {

        final Object collection = StatmentUtil.execute(collectionExpr, context);
        final Iter iter = CollectionUtil.toIter(collection);

        if (iter != null && iter.hasNext() && bodyStatment != null) {
            final LoopCtrl ctrl = context.loopCtrl;
            final Object[] params = new Object[2];
            params[1] = iter;
            label:
            while (iter.hasNext()) {
                params[0] = iter.next();
                bodyStatment.execute(context, paramIndexs, params);
                if (!ctrl.goon()) {
                    if (ctrl.matchLabel(label)) {
                        switch (ctrl.getLoopType()) {
                            case BREAK:
                                ctrl.reset();
                                break label; // while
                            case RETURN:
                                //can't deal
                                break label; //while
                            case CONTINUE:
                                ctrl.reset();
                                break; //switch
                            default:
                                break label; //while
                            }
                    } else {
                        break;
                    }
                }
            }
        } else if (elseStatment != null) {
            StatmentUtil.execute(elseStatment, context);
        }
        return null;
    }

    public Statment optimize() {
        return bodyStatment != null || elseStatment != null ? this : null;
    }

    public List<LoopInfo> collectPossibleLoopsInfo() {

        List<LoopInfo> list = null;
        if (bodyStatment != null) {
            list = bodyStatment.collectPossibleLoopsInfo();
            if (list != null) {

                for (Iterator<LoopInfo> it = list.iterator(); it.hasNext();) {
                    LoopInfo loopInfo = it.next();
                    if (loopInfo.matchLabel(this.label)
                            && (loopInfo.type == LoopType.BREAK
                            || loopInfo.type == LoopType.CONTINUE)) {
                        it.remove();
                    }
                }

                list = list.isEmpty() ? null : list;
            }
        }


        //
        if (elseStatment != null && elseStatment instanceof Loopable) {
            List<LoopInfo> list2 = ((Loopable) elseStatment).collectPossibleLoopsInfo();

            if (list == null) {
                list = list2;
            } else if (list2 != null) {
                list.addAll(list2);
            }
        }
        return list;
    }
}
