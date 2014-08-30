// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.Context;
import webit.script.core.ast.ResetableValueExpression;
import webit.script.core.ast.Statement;
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
public class RedirectOut extends Statement {

    private final Statement srcStatement;
    private final ResetableValueExpression toExpr;

    public RedirectOut(Statement srcStatement, ResetableValueExpression toExpr, int line, int column) {
        super(line, column);
        this.srcStatement = srcStatement;
        this.toExpr = toExpr;
    }

    public Object execute(final Context context) {
        final Out current = context.out;
        if (current.isByteStream()) {
            FastByteArrayOutputStream out = new FastByteArrayOutputStream(256);
            context.out = new OutputStreamOut(out, (OutputStreamOut) current);
            StatementUtil.execute(srcStatement, context);
            StatementUtil.executeSetValue(toExpr, context, out.toByteArray());
        } else {
            FastCharArrayWriter writer = new FastCharArrayWriter(256);
            context.out = current instanceof WriterOut
                    ? new WriterOut(writer, (WriterOut) current)
                    : new WriterOut(writer, context.encoding, context.template.engine.getCoderFactory());
            StatementUtil.execute(srcStatement, context);
            StatementUtil.executeSetValue(toExpr, context, writer.toCharArray());
        }
        context.out = current;
        return null;
    }
}
