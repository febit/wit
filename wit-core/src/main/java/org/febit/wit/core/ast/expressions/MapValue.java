// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.expressions;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.util.StatementUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zqq90
 */
public final class MapValue extends Expression {

    private final Expression[] keyExprs;
    private final Expression[] valueExprs;
    private final int initialCapacity;

    public MapValue(Expression[] keyExprs, Expression[] valueExprs, int line, int column) {
        super(line, column);
        StatementUtil.optimize(keyExprs);
        StatementUtil.optimize(valueExprs);
        this.keyExprs = keyExprs;
        this.valueExprs = valueExprs;
        this.initialCapacity = Math.max((keyExprs.length + 1) * 4 / 3, 4);
    }

    @Override
    public Object execute(final InternalContext context) {
        final Expression[] keys = this.keyExprs;
        final Expression[] values = this.valueExprs;
        final int len = values.length;
        final Map<Object, Object> value = new HashMap<>(initialCapacity, 0.75f);
        for (int i = 0; i < len; i++) {
            value.put(keys[i].execute(context), values[i].execute(context));
        }
        return value;
    }

    @Override
    public Object getConstValue() {
        final Expression[] keys = this.keyExprs;
        final Expression[] values = this.valueExprs;
        final int len = keys.length;
        final Map<Object, Object> value = new HashMap<>(initialCapacity, 0.75f);
        for (int i = 0; i < len; i++) {
            value.put(StatementUtil.calcConst(keys[i]),
                    StatementUtil.calcConst(values[i]));
        }
        return value;
    }
}
