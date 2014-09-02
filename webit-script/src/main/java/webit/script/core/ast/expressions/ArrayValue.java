// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.Expression;

/**
 *
 * @author Zqq
 */
public final class ArrayValue extends Expression {

    private final Expression[] valueExprs;

    public ArrayValue(Expression[] valueExprs, int line, int column) {
        super(line, column);
        this.valueExprs = valueExprs;
    }

    public Object execute(final Context context) {
        final Expression[] exprs;
        final int len;
        final Object[] value = new Object[len = (exprs = this.valueExprs).length];
        int i = 0;
        while (i < len) {
            value[i] = exprs[i].execute(context);
            i++;
        }
        return value;
    }
}
