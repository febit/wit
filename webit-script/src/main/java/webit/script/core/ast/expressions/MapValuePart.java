// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import java.util.LinkedList;
import java.util.List;
import webit.script.core.ast.Expression;
import webit.script.core.ast.StatmentPart;

/**
 *
 * @author Zqq
 */
public final class MapValuePart extends StatmentPart {

    protected final List keys;
    protected final List<Expression> valueExprs;

    public MapValuePart(int line, int column) {
        super(line, column);
        this.keys = new LinkedList();
        this.valueExprs = new LinkedList<Expression>();
    }

    @SuppressWarnings("unchecked")
    public MapValuePart append(Object key, Expression expr) {
        this.keys.add(key);
        this.valueExprs.add(expr);
        return this;
    }

    @SuppressWarnings("unchecked")
    public MapValue pop() {

        return new MapValue(
                keys.toArray(),
                valueExprs.toArray(new Expression[valueExprs.size()]),
                line, column);
    }
}
