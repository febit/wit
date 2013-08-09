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

    @Override
    public void execute(Context context) {
        context.loopCtrl.breakLoop(label, this);
    }
}
