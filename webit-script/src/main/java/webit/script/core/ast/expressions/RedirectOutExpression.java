// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.Expression;
import webit.script.core.ast.ResetableValueExpression;
import webit.script.io.Out;
import webit.script.io.impl.OutputStreamOut;
import webit.script.io.impl.WriterOut;
import webit.script.util.FastByteArrayOutputStream;
import webit.script.util.FastCharArrayWriter;
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

    public Object execute(final Context context) {

        final Out current = context.getOut();
        if (current instanceof OutputStreamOut) {

            FastByteArrayOutputStream out = new FastByteArrayOutputStream(256);

            Object result = StatmentUtil.execute(srcExpr, context, new OutputStreamOut(out, (OutputStreamOut) current));
            StatmentUtil.executeSetValue(toExpr, context, out.toByteArray());

            return result;
        } else {
            FastCharArrayWriter writer = new FastCharArrayWriter(256);

            Object result = StatmentUtil.execute(srcExpr, context,
                    current instanceof WriterOut
                    ? new WriterOut(writer, (WriterOut) current)
                    : new WriterOut(writer, context.encoding, context.template.engine.getCoderFactory()));
            
            StatmentUtil.executeSetValue(toExpr, context, writer.toCharArray());

            return result;
        }

    }
}
