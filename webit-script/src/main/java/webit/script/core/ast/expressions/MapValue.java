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

    private final static float DEFAULT_MAP_FACTOR = 0.75f;
    private final static int MIN_MAP_CAPACITY = 4;
    private final Object[] keys;
    private final Expression[] valueExprs;
    private final int initialCapacity;

    public MapValue(Object[] keys, Expression[] valueExprs, int line, int column) {
        super(line, column);
        this.keys = keys;
        this.valueExprs = valueExprs;
        int cap = (keys.length + 1) * 4 / 3;
        this.initialCapacity = cap > MIN_MAP_CAPACITY ? cap : MIN_MAP_CAPACITY;
    }

    @SuppressWarnings("unchecked")
    public Object execute(final Context context) {
        final int len = valueExprs.length;
        final Map value = new HashMap(initialCapacity, DEFAULT_MAP_FACTOR);
        for (int i = 0; i < len; i++) {
            value.put(keys[i], StatmentUtil.execute(valueExprs[i], context));
        }
        return value;
    }
}
