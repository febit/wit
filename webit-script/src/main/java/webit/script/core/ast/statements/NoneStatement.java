// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.Context;
import webit.script.core.ast.Optimizable;
import webit.script.core.ast.Statement;

/**
 *
 * @author zqq90
 */
public final class NoneStatement extends Statement implements Optimizable {

    public static final NoneStatement INSTANCE = new NoneStatement();

    private NoneStatement() {
        this(-1, -1);
    }

    public NoneStatement(int line, int column) {
        super(line, column);
    }

    @Override
    public Object execute(final Context context) {
        return null;
    }

    @Override
    public Statement optimize() {
        return null;
    }
}
