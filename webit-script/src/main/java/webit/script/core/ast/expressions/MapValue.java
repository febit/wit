// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import java.util.HashMap;
import java.util.Map;
import webit.script.Context;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.Expression;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class MapValue extends AbstractExpression {

    private final Object[] keys;
    private final Expression[] valueExprs;

    public MapValue(Object[] keys, Expression[] valueExprs, int line, int column) {
        super(line, column);
        this.keys = keys;
        this.valueExprs = valueExprs;
    }

    @SuppressWarnings("unchecked")
    public Object execute(Context context, boolean needReturn) {
        int len = valueExprs.length;
        Map value = new HashMap(keys.length);
        for (int i = 0; i < len; i++) {
            value.put(keys[i], StatmentUtil.execute(valueExprs[i], context));
        }
        return value;
    }
    //TODO:可优化成 CloneDirectValue
}
