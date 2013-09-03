// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.Expression;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class ArrayValue extends AbstractExpression {

    private final Expression[] valueExprs;

    public ArrayValue(Expression[] valueExprs, int line, int column) {
        super(line, column);
        this.valueExprs = valueExprs;
    }

    @Override
    public Object execute(Context context, boolean needReturn) {
        int len = valueExprs.length;
        Object[] value = new Object[len];
        for (int i = 0; i < len; i++) {
            value[i] = StatmentUtil.execute(valueExprs[i], context, true);
        }
        return value;
    }
    //TODO:可优化成 CloneDirectValue
}
