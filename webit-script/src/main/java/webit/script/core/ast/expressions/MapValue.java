// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import java.util.HashMap;
import java.util.Map;
import webit.script.Context;
import webit.script.core.ast.Constable;
import webit.script.core.ast.Expression;
import webit.script.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public final class MapValue extends Expression implements Constable {

    private final Object[] keys;
    private final Expression[] valueExprs;
    private final int initialCapacity;

    public MapValue(Object[] keys, Expression[] valueExprs, int line, int column) {
        super(line, column);
        StatementUtil.optimize(valueExprs);
        this.keys = keys;
        this.valueExprs = valueExprs;
        int cap = (keys.length + 1) * 4 / 3;
        this.initialCapacity = cap > 4 ? cap : 4;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object execute(final Context context) {
        final Object[] mapKeys = this.keys;
        final Expression[] exprs = this.valueExprs;
        final int len = exprs.length;
        final Map value = new HashMap(initialCapacity, 0.75f);
        for (int i = 0; i < len; i++) {
            value.put(mapKeys[i], exprs[i].execute(context));
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getConstValue() {

        final Object[] mapKeys = this.keys;
        final int len = mapKeys.length;
        final Object[] values = StatementUtil.calcConstArrayForce(this.valueExprs);
        final Map value = new HashMap(initialCapacity, 0.75f);
        for (int i = 0; i < len; i++) {
            value.put(mapKeys[i], values[i]);
        }
        return value;
    }
}