// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.oper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.AssignableExpression;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.util.Iters;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class GroupAssign implements Expression {

    private final AssignableExpression[] lefts;
    private final Expression right;
    @Getter
    private final Position position;

    @Override
    public Object execute(InternalContext context) {
        var values = right.execute(context);
        var iter = Iters.toIter(values, this);
        var assignables = this.lefts;

        final int resultLength = assignables.length;
        @Nullable
        Object[] result = new Object[resultLength];
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
