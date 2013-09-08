// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.ResetableValue;
import webit.script.core.ast.ResetableValueExpression;

/**
 *
 * @author Zqq
 */
public final class ContextValue extends AbstractExpression implements ResetableValueExpression {

    private final int upstairs;
    private final int index;
    private final String name;
    private final boolean withSuper;

    public ContextValue(int upstairs, int index, int line, int column) {
        this(upstairs, index, null, false, line, column);
    }

    public ContextValue(int upstairs, int index, String name, int line, int column) {
        this(upstairs, index, name, false, line, column);
    }

    public ContextValue(int upstairs, int index, String name, boolean withSuper, int line, int column) {
        super(line, column);
        this.upstairs = upstairs;
        this.index = index;
        this.withSuper = withSuper;
        this.name = name;
    }

    public Object get(Context context) {
        return context.vars.get(upstairs, index);
    }

    public void set(Context context, Object value) {
        context.vars.set(upstairs, index, value);
    }

    public Object execute(final Context context, final boolean needReturn) {
        return get(context);
    }

    public ResetableValue getResetableValue(final Context context) {
        return new ResetableValue() {
            public Object get() {
                return ContextValue.this.get(context);
            }

            public boolean set(Object value) {
                ContextValue.this.set(context, value);
                return true;
            }
        };
    }
}
