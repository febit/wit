// Copyright (c) 2013, Webit Team. All Rights Reserved.

package webit.script.core.ast.expressions;

import jodd.io.FastByteArrayOutputStream;
import jodd.io.FastCharArrayWriter;
import webit.script.Context;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.Expression;
import webit.script.core.ast.ResetableValue;
import webit.script.core.ast.ResetableValueExpression;
import webit.script.io.impl.OutputStreamOut;
import webit.script.io.impl.WriterOut;
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


        if (context.getOut() instanceof OutputStreamOut) {

            FastByteArrayOutputStream out = new FastByteArrayOutputStream(128);

            Object result = StatmentUtil.execute(srcExpr, context, needReturn, new OutputStreamOut(out, context.encoding));
            ResetableValue value = StatmentUtil.getResetableValue(toExpr, context);
            value.set(out.toByteArray());

            return result;
        } else {
            FastCharArrayWriter writer = new FastCharArrayWriter();

            Object result = StatmentUtil.execute(srcExpr, context, needReturn, new WriterOut(writer, context.encoding));
            ResetableValue value = StatmentUtil.getResetableValue(toExpr, context);
            value.set(writer.toString());

            return result;
        }

    }
}
