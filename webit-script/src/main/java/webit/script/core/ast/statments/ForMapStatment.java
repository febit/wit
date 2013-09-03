// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import java.util.Map;
import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Optimizable;
import webit.script.core.ast.Statment;
import webit.script.core.runtime.LoopCtrl;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.util.CollectionUtil;
import webit.script.util.StatmentUtil;
import webit.script.util.collection.Iter;

/**
 *
 * @author Zqq
 */
public final class ForMapStatment extends AbstractStatment implements Optimizable {

    private final int[] paramIndexs;
    private final Expression mapExpr;
    private final BlockStatment bodyStatment;
    private final Statment elseStatment;
    private final String label;
    //
    private final boolean bodyNotEmpty;
    private final boolean elseNotEmpty;

    public ForMapStatment(int keyIndex, int valueIndex, int iterIndex, Expression mapExpr, BlockStatment bodyStatment, Statment elseStatment, String label, int line, int column) {
        super(line, column);
        this.paramIndexs = new int[]{keyIndex, valueIndex, iterIndex};
        this.mapExpr = mapExpr;
        this.bodyStatment = bodyStatment;
        this.elseStatment = elseStatment;
        this.label = label;
        //
        bodyNotEmpty = bodyStatment != null;
        elseNotEmpty = elseStatment != null;
    }

    @Override
    public void execute(Context context) {
        Object object = StatmentUtil.execute(mapExpr, context);
        Iter<Map.Entry> iter;
        if (object != null) {
            if (object instanceof Map) {
                iter = CollectionUtil.toIter(((Map) object).entrySet());
            } else {
                throw new ScriptRuntimeException("not a instance of java.util.Map");
            }
        } else {
            iter = null;
        }
        if (iter != null && iter.hasNext() && bodyNotEmpty) {
            LoopCtrl ctrl = context.loopCtrl;
            label:
            while (iter.hasNext()) {

                Map.Entry entry = iter.next();
                bodyStatment.execute(context, paramIndexs, new Object[]{entry.getKey(), entry.getValue(), iter});

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
                        break; //while
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
