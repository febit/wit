// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.ResetableValueExpression;

/**
 *
 * @author Zqq
 */
public final class RootContextValue extends AbstractExpression implements ResetableValueExpression {

    private final int index;

    public RootContextValue(int index, int line, int column) {
        super(line, column);
        this.index = index;
    }

    public Object execute(final Context context) {
        return context.vars.getFromRoot(index);
    }

    public Object setValue(final Context context, final Object value) {
        context.vars.setToRoot(index, value);
        return value;
    }
}
