// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import java.util.HashMap;
import java.util.Map;
import webit.script.Context;
import webit.script.core.ast.Expression;

/**
 *
 * @author Zqq
 */
public final class MapValue extends Expression {

    private static final float DEFAULT_MAP_FACTOR = 0.75f;
    private static final int MIN_MAP_CAPACITY = 4;
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
        final Object[] keys = this.keys;
        final Expression[] valueExprs = this.valueExprs;
        final int len = valueExprs.length;
        final Map value = new HashMap(initialCapacity, DEFAULT_MAP_FACTOR);
        for (int i = 0; i < len; i++) {
            value.put(keys[i], valueExprs[i].execute(context));
        }
        return value;
    }
}
