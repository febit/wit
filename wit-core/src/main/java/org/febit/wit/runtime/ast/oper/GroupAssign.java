// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.oper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AssignableExpression;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.util.Iters;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class GroupAssign implements Expression {

    private final AssignableExpression[] targets;
    private final Expression right;
    @Getter
    private final Position position;

    @Override
    public Object execute(InternalContext context) {
        var values = right.execute(context);
        var iter = Iters.toIter(values, this);
        var assignables = this.targets;

        final int resultLength = assignables.length;
        var result = new Object[resultLength];
        int current = 0;
        while (iter.hasNext() && current < resultLength) {
            Object next = iter.next();
            result[current] = assignables[current].set(context, next);
            current++;
        }
        for (; current < resultLength; current++) {
            assignables[current].set(context, null);
            result[current] = null;
        }
        return result;
    }
}
