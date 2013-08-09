package webit.script.core.ast.expressions;

import jodd.io.FastByteArrayOutputStream;
import webit.script.Context;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.Expression;
import webit.script.core.ast.ResetableValue;
import webit.script.core.ast.ResetableValueExpression;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public class RedirectOutExpression extends AbstractExpression {

    private final Expression srcExpr;
    private final ResetableValueExpression toExpr;

    public RedirectOutExpression(Expression srcExpr, ResetableValueExpression toExpr, int line, int column) {
        super(line, column);
        this.srcExpr = srcExpr;
        this.toExpr = toExpr;
    }

    @Override
    public Object execute(Context context, boolean needReturn) {

        FastByteArrayOutputStream out = new FastByteArrayOutputStream(128);

        Object result =  StatmentUtil.execute(srcExpr, context, needReturn, out);

        ResetableValue value =  StatmentUtil.getResetableValue(toExpr, context);

        value.set(out.toByteArray());
        return result;
    }
}
