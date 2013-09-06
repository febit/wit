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
public final class NoneStatment extends AbstractStatment implements Optimizable{

    public NoneStatment() {
        this(-1, -1);
    }

    public NoneStatment(int line, int column) {
        super(line, column);
    }

    public void execute(Context context) {
        //
    }
    private static NoneStatment instance;

    public static NoneStatment getInstance() {
        if (instance == null) {
            instance = new NoneStatment();
        }
        return instance;
    }

    public Statment optimize() {
        return null;
    }
}
