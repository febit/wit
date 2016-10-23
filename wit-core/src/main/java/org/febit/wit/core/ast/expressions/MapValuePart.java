// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.expressions;

import java.util.ArrayList;
import java.util.List;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public final class MapValuePart {

    private final List keys;
    private final List<Expression> valueExprs;

    public MapValuePart() {
        this.keys = new ArrayList();
        this.valueExprs = new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    public MapValuePart add(Object key, Expression expr) {
        this.keys.add(key);
        this.valueExprs.add(StatementUtil.optimize(expr));
        return this;
    }

    @SuppressWarnings("unchecked")
    public MapValue pop(int line, int column) {
        return new MapValue(
                keys.toArray(),
                valueExprs.toArray(new Expression[valueExprs.size()]),
                line, column);
    }
}
