// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.Context;
import webit.script.core.ast.ResetableValueExpression;
import webit.script.core.ast.Statement;
import webit.script.io.Out;
import webit.script.io.impl.OutputStreamOut;
import webit.script.io.impl.WriterOut;
import webit.script.util.ByteArrayOutputStream;
import webit.script.util.CharArrayWriter;

/**
 *
 * @author Zqq
 */
public class RedirectOut extends Statement {

    private final Statement srcStatement;
    private final ResetableValueExpression toExpr;

    public RedirectOut(Statement srcStatement, ResetableValueExpression toExpr, int line, int column) {
        super(line, column);
        this.srcStatement = srcStatement;
        this.toExpr = toExpr;
    }

    @Override
    public Object execute(final Context context) {
        final Out current = context.out;
        if (current.isByteStream()) {
            ByteArrayOutputStream out = new ByteArrayOutputStream(256);
            context.out = new OutputStreamOut(out, (OutputStreamOut) current);
            srcStatement.execute(context);
            toExpr.setValue(context, out.toArray());
        } else {
            CharArrayWriter writer = new CharArrayWriter(256);
            context.out = current instanceof WriterOut
                    ? new WriterOut(writer, (WriterOut) current)
                    : new WriterOut(writer, context.encoding, context.template.engine.getCoderFactory());
            srcStatement.execute(context);
            toExpr.setValue(context, writer.toArray());
        }
        context.out = current;
        return null;
    }
}
