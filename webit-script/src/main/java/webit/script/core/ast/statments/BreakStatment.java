// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import webit.script.Context;
import webit.script.core.ast.AbstractStatment;

/**
 *
 * @author Zqq
 */
public final class BreakStatment extends AbstractStatment {

    private final String label;

    public BreakStatment(String label, int line, int column) {
        super(line, column);
        this.label = label;
    }

    public Object execute(final Context context) {
        context.loopCtrl.breakLoop(label, this);
        return null;
    }
}
