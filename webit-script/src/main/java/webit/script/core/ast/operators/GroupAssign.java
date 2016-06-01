// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.operators;

import webit.script.Context;
import webit.script.core.ast.Expression;
import webit.script.core.ast.ResetableValueExpression;
import webit.script.lang.Iter;
import webit.script.util.CollectionUtil;

/**
 *
 * @author zqq90
 */
public final class GroupAssign extends Expression {

    private final Expression rexpr;
    private final ResetableValueExpression[] lexpres;

    public GroupAssign(ResetableValueExpression[] lexpres, Expression rexpr, int line, int column) {
        super(line, column);
        this.lexpres = lexpres;
        this.rexpr = rexpr;
    }

    @Override
    public Object execute(final Context context) {
        final Object values = rexpr.execute(context);
        final Iter iter = CollectionUtil.toIter(values, this);

        final ResetableValueExpression[] lexpres = this.lexpres;
        final int resultLength = lexpres.length;
        final Object[] result = new Object[resultLength];

        int current = 0;

        while (iter.hasNext() && current < resultLength) {
            Object next = iter.next();
            result[current] = lexpres[current].setValue(context, next);
            current++;
        }

        for (; current < resultLength; current++) {
            lexpres[current].setValue(context, null);
            result[current] = null;
        }
        return result;
    }
}
