// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.ResetableValueExpression;

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
    public Object execute(final Context context) {
        return context.parentScopes[scope][index];
    }

    @Override
    public Object setValue(final Context context, final Object value) {
        return context.parentScopes[scope][index] = value;
    }
}
