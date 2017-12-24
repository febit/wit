// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.expressions;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.AssignableExpression;

/**
 *
 * @author zqq90
 */
public final class ContextValue extends AssignableExpression {

    private final int index;

    public ContextValue(int index, int line, int column) {
        super(line, column);
        this.index = index;
    }

    @Override
    public Object execute(final InternalContext context) {
        return context.vars[index];
    }

    @Override
    public Object setValue(final InternalContext context, final Object value) {
        context.vars[index] = value;
        return value;
    }
}
