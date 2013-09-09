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
public final class ArrayValuePart extends StatmentPart {

    protected final List<Expression> valueExprs;

    public ArrayValuePart(int line, int column) {
        super(line, column);
        this.valueExprs = new LinkedList<Expression>();
    }

    public ArrayValuePart append(Expression expr) {
        this.valueExprs.add(expr);
        return this;
    }

    public ArrayValue pop() {
        return new ArrayValue(
                valueExprs.toArray(new Expression[valueExprs.size()]),
                line, column);
    }
}
