// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statment;
import webit.script.util.CollectionUtil;
import webit.script.util.StatmentUtil;
import webit.script.util.collection.Iter;

/**
 *
 * @author Zqq
 */
public final class ForInStatmentNoLoops extends AbstractStatment {

    private final int[] paramIndexs;
    private final Expression collectionExpr;
    private final BlockStatment bodyStatment;
    private final Statment elseStatment;

    public ForInStatmentNoLoops(int itemIndex, int iterIndex, Expression collectionExpr, BlockStatment bodyStatment, Statment elseStatment, int line, int column) {
        super(line, column);
        this.paramIndexs = new int[]{itemIndex, iterIndex};
        this.collectionExpr = collectionExpr;
        this.bodyStatment = bodyStatment;
        this.elseStatment = elseStatment;
    }

    public Object execute(final Context context) {

        final Iter iter = CollectionUtil.toIter(
                StatmentUtil.execute(collectionExpr, context));

        if (iter != null && iter.hasNext()) {
            final Object[] params = new Object[2];
            params[1] = iter;
            do {
                params[0] = iter.next();
                bodyStatment.execute(context, paramIndexs, params);
            } while (iter.hasNext());

        } else if (elseStatment != null) {
            StatmentUtil.execute(elseStatment, context);
        }
        return null;
    }
}
