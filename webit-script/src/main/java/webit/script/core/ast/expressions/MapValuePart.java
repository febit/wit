// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import java.util.ArrayList;
import java.util.List;
import webit.script.core.ast.Expression;

/**
 *
 * @author Zqq
 */
public final class MapValuePart {

    private final List keys;
    private final List<Expression> valueExprs;

    public MapValuePart() {
        this.keys = new ArrayList();
        this.valueExprs = new ArrayList<Expression>();
    }

    @SuppressWarnings("unchecked")
    public MapValuePart add(Object key, Expression expr) {
        this.keys.add(key);
        this.valueExprs.add(expr);
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
