// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast;

import webit.script.Context;

/**
 *
 * @author Zqq
 */
public abstract class Statement {

    public final int line;
    public final int column;

    protected Statement(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public abstract Object execute(Context context);
}
