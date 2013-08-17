// Copyright (c) 2013, Webit Team. All Rights Reserved.

package webit.script.core.ast.statments;

import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Optimizable;
import webit.script.core.ast.Statment;
import webit.script.core.runtime.LoopCtrl;
import webit.script.util.CollectionUtil;
import webit.script.util.StatmentUtil;
import webit.script.util.collection.Iter;

/**
 *
 * @author Zqq
 */
public final class ForInStatment extends AbstractStatment implements Optimizable {

//    private final int itemIndex;
//    private final int iterIndex;
    private final int[] paramIndexs;
    private final Expression collectionExpr;
    private final BlockStatment bodyStatment;
    private final Statment elseStatment;
    private final String label;
    //
    private final boolean bodyNotEmpty;
    private final boolean elseNotEmpty;

    public ForInStatment(int itemIndex, int iterIndex, Expression collectionExpr, BlockStatment bodyStatment, Statment elseStatment, String label, int line, int column) {
        super(line, column);
//        this.itemIndex = itemIndex;
//        this.iterIndex = iterIndex;
        this.paramIndexs = new int[]{itemIndex, iterIndex};
        this.collectionExpr = collectionExpr;
        this.bodyStatment = bodyStatment;
        this.elseStatment = elseStatment;
        this.label = label;
        //
        bodyNotEmpty = bodyStatment != null;
        elseNotEmpty = elseStatment != null;
    }

    @Override
    public void execute(Context context) {

        Object collection = StatmentUtil.execute(collectionExpr, context);
        Iter iter = CollectionUtil.toIter(collection);
        if (iter != null && iter.hasNext() && bodyNotEmpty) {
            LoopCtrl ctrl = context.loopCtrl;
            label:
            while (iter.hasNext()) {

                Object object = iter.next();
                bodyStatment.execute(context, paramIndexs, new Object[]{object, iter});
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
        } else if (elseNotEmpty) {
            StatmentUtil.execute(elseStatment, context);
        }
    }

    public Statment optimize() {
        if (bodyNotEmpty || elseNotEmpty) {
            return this;
        }
        return null;
    }
}
