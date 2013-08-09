package webit.script.core.ast.statments;

import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Expression;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class ReturnStatment extends AbstractStatment {

    private final Expression expr;

    public ReturnStatment(Expression expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    @Override
    public void execute(Context context) {
        Object result = expr != null ? StatmentUtil.execute(expr, context) : Context.VOID;
        context.loopCtrl.returnLoop(result, this);
    }
}
