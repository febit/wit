// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast;

import org.febit.wit.exceptions.ParseException;

/**
 * @author zqq90
 */
public abstract class Expression extends Statement {

    protected Expression(int line, int column) {
        super(line, column);
    }

    public Object getConstValue() {
        throw new ParseException("Can't get a const value from this expression.", line, column);
    }

    @Override
    public Expression optimize() {
        return this;
    }
}
