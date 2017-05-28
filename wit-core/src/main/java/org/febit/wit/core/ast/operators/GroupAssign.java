// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.operators;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.core.ast.ResetableValueExpression;
import org.febit.wit.lang.Iter;
import org.febit.wit.util.CollectionUtil;

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
    public Object execute(final InternalContext context) {
        final Object values = rexpr.execute(context);
        final Iter iter = CollectionUtil.toIter(values, this);
        final ResetableValueExpression[] resetables = this.lexpres;
        final int resultLength = resetables.length;
        final Object[] result = new Object[resultLength];
        int current = 0;
        while (iter.hasNext() && current < resultLength) {
            Object next = iter.next();
            result[current] = resetables[current].setValue(context, next);
            current++;
        }
        for (; current < resultLength; current++) {
            resetables[current].setValue(context, null);
            result[current] = null;
        }
        return result;
    }
}
