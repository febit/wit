// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.Constable;
import webit.script.core.ast.Expression;
import webit.script.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public final class ArrayValue extends Expression implements Constable {

    private final Expression[] valueExprs;

    public ArrayValue(Expression[] valueExprs, int line, int column) {
        super(line, column);
        StatementUtil.optimize(valueExprs);
        this.valueExprs = valueExprs;
    }

    @Override
    public Object execute(final Context context) {
        final Expression[] exprs = this.valueExprs;
        final int len = exprs.length;
        final Object[] value = new Object[len];
        for (int i = 0; i < len; i++) {
            value[i] = exprs[i].execute(context);
        }
        return value;
    }

    @Override
    public Object getConstValue() {
        return StatementUtil.calcConstArrayForce(this.valueExprs);
    }
}
