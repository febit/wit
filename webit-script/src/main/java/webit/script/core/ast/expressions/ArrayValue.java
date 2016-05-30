// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Optimizable;
import webit.script.core.ast.Statement;
import webit.script.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public final class ArrayValue extends Expression implements Optimizable {

    private final Expression[] valueExprs;

    public ArrayValue(Expression[] valueExprs, int line, int column) {
        super(line, column);
        this.valueExprs = valueExprs;
    }

    @Override
    public Object execute(final Context context) {
        final Expression[] exprs;
        final int len;
        final Object[] value = new Object[len = (exprs = this.valueExprs).length];
        for (int i = 0; i < len; i++) {
            value[i] = exprs[i].execute(context);
        }
        return value;
    }

    @Override
    public Statement optimize() throws Exception {
        final Expression[] exprs = this.valueExprs;
        StatementUtil.optimize(exprs);
        final int len = exprs.length;
        for (int i = 0; i < len; i++) {
            if (!(exprs[i] instanceof DirectValue)) {
                return this;
            }
        }
        final Object[] value = new Object[len];
        for (int i = 0; i < len; i++) {
            value[i] = ((DirectValue) exprs[i]).value;
        }
        return new DirectValue(value, line, column);
    }
}
