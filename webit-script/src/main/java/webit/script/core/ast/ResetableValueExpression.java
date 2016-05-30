// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast;

import webit.script.Context;

/**
 *
 * @author zqq90
 */
public abstract class ResetableValueExpression extends Expression {

    protected ResetableValueExpression(int line, int column) {
        super(line, column);
    }

    public abstract Object setValue(Context context, Object value);
}
