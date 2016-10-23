// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast;

import org.febit.wit.InternalContext;

/**
 *
 * @author zqq90
 */
public abstract class Statement {

    public final int line;
    public final int column;

    protected Statement(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public abstract Object execute(InternalContext context);
}
