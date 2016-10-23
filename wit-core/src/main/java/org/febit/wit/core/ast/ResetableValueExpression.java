// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast;

import org.febit.wit.InternalContext;

/**
 *
 * @author zqq90
 */
public abstract class ResetableValueExpression extends Expression {

    protected ResetableValueExpression(int line, int column) {
        super(line, column);
    }

    public abstract Object setValue(InternalContext context, Object value);
}
