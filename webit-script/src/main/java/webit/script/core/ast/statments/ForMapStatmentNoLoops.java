// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import java.util.Map;
import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statment;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.util.CollectionUtil;
import webit.script.util.StatmentUtil;
import webit.script.util.collection.Iter;

/**
 *
 * @author Zqq
 */
public final class ForMapStatmentNoLoops extends AbstractStatment {

    private final int[] paramIndexs;
    private final Expression mapExpr;
    private final BlockStatment bodyStatment;
    private final Statment elseStatment;

    public ForMapStatmentNoLoops(int keyIndex, int valueIndex, int iterIndex, Expression mapExpr, BlockStatment bodyStatment, Statment elseStatment, int line, int column) {
        super(line, column);
        this.paramIndexs = new int[]{keyIndex, valueIndex, iterIndex};
        this.mapExpr = mapExpr;
        this.bodyStatment = bodyStatment;
        this.elseStatment = elseStatment;
    }

    @SuppressWarnings("unchecked")
    public Object execute(final Context context) {
        final Object object = StatmentUtil.execute(mapExpr, context);
        final Iter<Map.Entry> iter;
        if (object != null) {
            if (object instanceof Map) {
                iter = CollectionUtil.toIter(((Map) object).entrySet());
            } else {
                throw new ScriptRuntimeException("Not a instance of java.util.Map");
            }
        } else {
            iter = null;
        }
        if (iter != null && iter.hasNext()) {
            final Object[] params = new Object[3];
            params[2] = iter;
            Map.Entry entry;
            do {
                entry = iter.next();
                params[0] = entry.getKey();
                params[1] = entry.getValue();
                bodyStatment.execute(context, paramIndexs, params);
            } while (iter.hasNext());

        } else if (elseStatment != null) {
            StatmentUtil.execute(elseStatment, context);
        }
        return null;
    }
}
