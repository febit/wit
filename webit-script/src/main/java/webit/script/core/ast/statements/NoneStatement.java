// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.Context;
import webit.script.core.ast.AbstractStatement;
import webit.script.core.ast.Optimizable;
import webit.script.core.ast.Statement;

/**
 *
 * @author Zqq
 */
public final class NoneStatement extends AbstractStatement implements Optimizable{

    public NoneStatement() {
        this(-1, -1);
    }

    public NoneStatement(int line, int column) {
        super(line, column);
    }

    public Object execute(final Context context) {
        return null;
    }
    private static NoneStatement instance;

    public static NoneStatement getInstance() {
        if (instance == null) {
            instance = new NoneStatement();
        }
        return instance;
    }

    public Statement optimize() {
        return null;
    }
}
