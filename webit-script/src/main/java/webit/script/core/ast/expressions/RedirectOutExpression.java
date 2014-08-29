// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.Expression;
import webit.script.core.ast.ResetableValueExpression;
import webit.script.io.Out;
import webit.script.io.impl.OutputStreamOut;
import webit.script.io.impl.WriterOut;
import webit.script.util.FastByteArrayOutputStream;
import webit.script.util.FastCharArrayWriter;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public class RedirectOutExpression extends Expression {

    private final Expression srcExpr;
    private final ResetableValueExpression toExpr;

    public RedirectOutExpression(Expression srcExpr, ResetableValueExpression toExpr, int line, int column) {
        super(line, column);
        this.srcExpr = srcExpr;
        this.toExpr = toExpr;
    }

    public Object execute(final Context context) {

        final Out current;
        final Object result;
        if ((current = context.getOut()).isByteStream()) {

            FastByteArrayOutputStream out = new FastByteArrayOutputStream(256);

            result = StatementUtil.execute(srcExpr, context, new OutputStreamOut(out, (OutputStreamOut) current));
            StatementUtil.executeSetValue(toExpr, context, out.toByteArray());

            return result;
        } else {
            FastCharArrayWriter writer = new FastCharArrayWriter(256);

            result = StatementUtil.execute(srcExpr, context,
                    current instanceof WriterOut
                    ? new WriterOut(writer, (WriterOut) current)
                    : new WriterOut(writer, context.encoding, context.template.engine.getCoderFactory()));
            
            StatementUtil.executeSetValue(toExpr, context, writer.toCharArray());

            return result;
        }

    }
}
