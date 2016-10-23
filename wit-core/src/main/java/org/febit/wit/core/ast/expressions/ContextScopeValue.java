// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.expressions;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.ResetableValueExpression;

/**
 *
 * @author zqq90
 */
public final class ContextScopeValue extends ResetableValueExpression {

    private final int index;
    private final int scope;

    public ContextScopeValue(int scope, int index,int line, int column) {
        super(line, column);
        this.scope = scope;
        this.index = index;
    }

    @Override
    public Object execute(final InternalContext context) {
        return context.parentScopes[scope][index];
    }

    @Override
    public Object setValue(final InternalContext context, final Object value) {
        return context.parentScopes[scope][index] = value;
    }
}
