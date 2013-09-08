// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.AbstractExpression;

/**
 *
 * @author Zqq
 */
public final class DirectValue extends AbstractExpression {

    public final Object value;

    public DirectValue(Object value, int line, int column) {
        super(line, column);
        this.value = value;
    }

    public Object execute(final Context context, final boolean needReturn) {
        return value;
    }
}
