// Copyright (c) 2013, Webit Team. All Rights Reserved.
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
//    public final String name;
//    private final boolean withSuper;

    public ContextValue(int upstairs, int index, String name, int line, int column) {
        this(upstairs, index, name, false, line, column);
    }

    public ContextValue(int upstairs, int index, String name, boolean withSuper, int line, int column) {
        super(line, column);
        this.upstairs = upstairs;
        this.index = index;
//        this.withSuper = withSuper;
//        this.name = name;
    }

    public Object execute(final Context context) {
        return context.vars.get(upstairs, index);
    }

    public Object setValue(final Context context, final Object value) {
        context.vars.set(upstairs, index, value);
        return value;
    }
}
