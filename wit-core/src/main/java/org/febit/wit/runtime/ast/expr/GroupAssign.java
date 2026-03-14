// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.expr;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AssignableExpression;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.iter.Iters;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class GroupAssign implements Expression {

    private final AssignableExpression[] targets;
    private final Expression value;
    @Getter
    private final Position position;

    @Override
    public Object execute(InternalContext context) {
        var values = value.execute(context);
        var iter = Iters.ofIter(values, this);
        var targetsObj = this.targets;

        final int targetSize = targetsObj.length;
        var results = new Object[targetSize];
        int current = 0;
        while (iter.hasNext() && current < targetSize) {
            Object next = iter.next();
            results[current] = targetsObj[current].assign(context, next);
            current++;
        }
        for (; current < targetSize; current++) {
            targetsObj[current].assign(context, null);
            results[current] = null;
        }
        return results;
    }
}
