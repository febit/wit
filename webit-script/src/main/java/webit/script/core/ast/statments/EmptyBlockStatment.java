// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Optimizable;
import webit.script.core.ast.Statment;

/**
 *
 * @author Zqq
 */
public class EmptyBlockStatment extends AbstractStatment implements BlockStatment, Optimizable {

    public EmptyBlockStatment(int line, int column) {
        super(line, column);
    }

    public void execute(Context context, int[] indexs, Object[] values) {
    }

    public boolean hasLoops() {
        return false;
    }

    public Object execute(Context context) {
        return null;
    }

    public Statment optimize() throws Throwable {
        return null;
    }
}
