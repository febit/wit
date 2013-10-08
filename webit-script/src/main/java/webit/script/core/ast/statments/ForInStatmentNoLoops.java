// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statment;
import webit.script.core.runtime.variant.VariantMap;
import webit.script.core.runtime.variant.VariantStack;
import webit.script.util.CollectionUtil;
import webit.script.util.StatmentUtil;
import webit.script.util.collection.Iter;

/**
 *
 * @author Zqq
 */
public final class ForInStatmentNoLoops extends AbstractStatment {

    private final int iterIndex;
    private final int itemIndex;
    private final Expression collectionExpr;
    private final VariantMap varMap;
    private final Statment[] statments;
    private final Statment elseStatment;

    public ForInStatmentNoLoops(int iterIndex, int itemIndex, Expression collectionExpr, VariantMap varMap, Statment[] statments, Statment elseStatment, int line, int column) {
        super(line, column);
        this.iterIndex = iterIndex;
        this.itemIndex = itemIndex;
        this.collectionExpr = collectionExpr;
        this.varMap = varMap;
        this.statments = statments;
        this.elseStatment = elseStatment;
    }

    public Object execute(final Context context) {
        final Iter iter;

        if ((iter = CollectionUtil.toIter(
                StatmentUtil.execute(collectionExpr, context))) != null
                && iter.hasNext()) {

            final Statment[] statments = this.statments;
            final VariantStack vars;
            (vars = context.vars).push(varMap);
            do {
                vars.resetCurrentWith(iterIndex, iter, itemIndex, iter.next());
                StatmentUtil.executeInverted(statments, context);
            } while (iter.hasNext());
            vars.pop();
            return null;
        } else if (elseStatment != null) {
            StatmentUtil.execute(elseStatment, context);
        }
        return null;
    }
}
