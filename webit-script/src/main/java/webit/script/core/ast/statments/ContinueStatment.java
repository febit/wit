// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import webit.script.Context;
import webit.script.core.ast.AbstractStatment;

/**
 *
 * @author Zqq
 */
public final class ContinueStatment extends AbstractStatment {

    private final String label;

    public ContinueStatment(String label, int line, int column) {
        super(line, column);
        this.label = label;
    }

    @Override
    public void execute(Context context) {
        context.loopCtrl.continueLoop(label, this);
    }
}
