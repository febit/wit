package webit.script.core.ast.statments;

import jodd.io.FastByteArrayOutputStream;
import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.ResetableValue;
import webit.script.core.ast.ResetableValueExpression;
import webit.script.core.ast.Statment;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public class RedirectOutStatment extends AbstractStatment {

    private final Statment srcStatment;
    private final ResetableValueExpression toExpr;

    public RedirectOutStatment(Statment srcStatment, ResetableValueExpression toExpr, int line, int column) {
        super(line, column);
        this.srcStatment = srcStatment;
        this.toExpr = toExpr;
    }

    @Override
    public void execute(Context context) {


        FastByteArrayOutputStream out = new FastByteArrayOutputStream(128);

        StatmentUtil.execute(srcStatment, out, context);

        ResetableValue value = StatmentUtil.getResetableValue(toExpr, context);

        value.set(out.toByteArray());
    }
}
