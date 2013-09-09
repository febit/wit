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

    public ForMapStatment(int keyIndex, int valueIndex, int iterIndex, Expression mapExpr, BlockStatment bodyStatment, Statment elseStatment, String label, int line, int column) {
        super(line, column);
        this.paramIndexs = new int[]{keyIndex, valueIndex, iterIndex};
        this.mapExpr = mapExpr;
        this.bodyStatment = bodyStatment;
        this.elseStatment = elseStatment;
        this.label = label;
    }

    @SuppressWarnings("unchecked")
    public void execute(final Context context) {
        final Object object = StatmentUtil.execute(mapExpr, context);
        final Iter<Map.Entry> iter;
        if (object != null) {
            if (object instanceof Map) {
                iter = CollectionUtil.toIter(((Map) object).entrySet());
            } else {
                throw new ScriptRuntimeException("not a instance of java.util.Map");
            }
        } else {
            iter = null;
        }
        if (iter != null && iter.hasNext() && bodyStatment != null) {
            final LoopCtrl ctrl = context.loopCtrl;
            final Object[] params = new Object[3];
            params[2] = iter;
            Map.Entry entry;
            label:
            while (iter.hasNext()) {

                entry = iter.next();
                params[0] = entry.getKey();
                params[1] = entry.getValue();

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
                        break; //while
                    }
                }
            }
        } else if (elseStatment != null) {
            StatmentUtil.execute(elseStatment, context);
        }
    }

    public Statment optimize() {
        return bodyStatment != null || elseStatment != null ? this : null;
    }
}
