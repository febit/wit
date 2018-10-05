// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast;

import org.febit.wit.InternalContext;

/**
 * @author zqq90
 */
public abstract class AssignableExpression extends Expression {

    protected AssignableExpression(int line, int column) {
        super(line, column);
    }

    public abstract Object setValue(InternalContext context, Object value);
}
