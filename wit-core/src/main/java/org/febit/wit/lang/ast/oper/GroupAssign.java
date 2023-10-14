// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.oper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.AssignableExpression;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.util.CollectionUtil;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public final class GroupAssign implements Expression {

    private final AssignableExpression[] lexpres;
    private final Expression rexpr;
    @Getter
    private final Position position;

    @Override
    public Object execute(final InternalContext context) {
        var values = rexpr.execute(context);
        var iter = CollectionUtil.toIter(values, this);
        var assignables = this.lexpres;
        final int resultLength = assignables.length;
        final Object[] result = new Object[resultLength];
        int current = 0;
        while (iter.hasNext() && current < resultLength) {
            Object next = iter.next();
            result[current] = assignables[current].setValue(context, next);
            current++;
        }
        for (; current < resultLength; current++) {
            assignables[current].setValue(context, null);
            result[current] = null;
        }
        return result;
    }
}
