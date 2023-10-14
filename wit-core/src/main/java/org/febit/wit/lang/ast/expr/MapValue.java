// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.expr;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.util.StatementUtil;

import java.util.HashMap;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public final class MapValue implements Expression {

    private final Expression[] keyExprs;
    private final Expression[] valueExprs;
    @Getter
    private final Position position;

    @Override
    public Object execute(final InternalContext context) {
        var keys = this.keyExprs;
        var values = this.valueExprs;
        var len = values.length;
        var initialCapacity = Math.max((len + 1) * 4 / 3, 4);
        var result = new HashMap<>(initialCapacity, 0.75f);
        for (int i = 0; i < len; i++) {
            result.put(keys[i].execute(context), values[i].execute(context));
        }
        return result;
    }

    @Override
    public Object getConstValue() {
        var keys = this.keyExprs;
        var values = this.valueExprs;
        var len = keys.length;
        var initialCapacity = Math.max((len + 1) * 4 / 3, 4);
        var result = new HashMap<>(initialCapacity, 0.75f);
        for (int i = 0; i < len; i++) {
            result.put(StatementUtil.calcConst(keys[i]),
                    StatementUtil.calcConst(values[i]));
        }
        return result;
    }
}
