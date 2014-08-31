// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.Expression;
import webit.script.core.ast.ResetableValueExpression;
import webit.script.io.Out;
import webit.script.io.impl.OutputStreamOut;
import webit.script.io.impl.WriterOut;
import webit.script.util.ByteArrayOutputStream;
import webit.script.util.CharArrayWriter;
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

        final Out current = context.out;
        final Object result;
        if (current.isByteStream()) {
            ByteArrayOutputStream out = new ByteArrayOutputStream(256);
            context.out = new OutputStreamOut(out, (OutputStreamOut) current);
            result = StatementUtil.execute(srcExpr, context);
            StatementUtil.executeSetValue(toExpr, context, out.toArray());
        } else {
            CharArrayWriter writer = new CharArrayWriter(256);
            context.out = current instanceof WriterOut
                    ? new WriterOut(writer, (WriterOut) current)
                    : new WriterOut(writer, context.encoding, context.template.engine.getCoderFactory());
            result = StatementUtil.execute(srcExpr, context);
            StatementUtil.executeSetValue(toExpr, context, writer.toArray());
        }
        context.out = current;
        return result;
    }
}
