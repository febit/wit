// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.ResetableValueExpression;

/**
 *
 * @author Zqq
 */
public final class ContextValue extends AbstractExpression implements ResetableValueExpression {

    private final int upstairs;
    private final int index;

    public ContextValue(int upstairs, int index, int line, int column) {
        super(line, column);
        this.upstairs = upstairs;
        this.index = index;
    }

    public Object execute(final Context context) {
        return context.get(upstairs, index);
    }

    public Object setValue(final Context context, final Object value) {
        context.set(upstairs, index, value);
        return value;
    }
}
