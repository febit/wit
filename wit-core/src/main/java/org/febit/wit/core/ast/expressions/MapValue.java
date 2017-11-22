// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.expressions;

import java.util.HashMap;
import java.util.Map;
import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.Constable;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public final class MapValue extends Expression implements Constable {

    private final Expression[] _keyExprs;
    private final Expression[] _valueExprs;
    private final int initialCapacity;

    public MapValue(Expression[] keyExprs, Expression[] valueExprs, int line, int column) {
        super(line, column);
        StatementUtil.optimize(keyExprs);
        StatementUtil.optimize(valueExprs);
        this._keyExprs = keyExprs;
        this._valueExprs = valueExprs;
        int cap = (keyExprs.length + 1) * 4 / 3;
        this.initialCapacity = cap > 4 ? cap : 4;
    }

    @Override
    public Object execute(final InternalContext context) {
        final Expression[] keyExprs = this._keyExprs;
        final Expression[] valueExprs = this._valueExprs;
        final int len = valueExprs.length;
        final Map<Object, Object> value = new HashMap<>(initialCapacity, 0.75f);
        for (int i = 0; i < len; i++) {
            value.put(keyExprs[i].execute(context), valueExprs[i].execute(context));
        }
        return value;
    }

    @Override
    public Object getConstValue() {
        final Expression[] keyExprs = this._keyExprs;
        final Expression[] valueExprs = this._valueExprs;
        final int len = keyExprs.length;
        final Map<Object, Object> value = new HashMap<>(initialCapacity, 0.75f);
        for (int i = 0; i < len; i++) {
            value.put(StatementUtil.calcConst(keyExprs[i], true),
                    StatementUtil.calcConst(valueExprs[i], true));
        }
        return value;
    }
}
