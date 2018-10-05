// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.expressions;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.AssignableExpression;

/**
 * @author zqq90
 */
public final class ContextScopeValue extends AssignableExpression {

    private final int scope;
    private final int index;

    public ContextScopeValue(int scope, int index, int line, int column) {
        super(line, column);
        this.scope = scope;
        this.index = index;
    }

    @Override
    public Object execute(final InternalContext context) {
        return context.getParentScopeValue(scope, index);
    }

    @Override
    public Object setValue(final InternalContext context, final Object value) {
        context.setParentScopeValue(scope, index, value);
        return value;
    }
}
