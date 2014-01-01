// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.Context;
import webit.script.core.ast.AbstractStatement;
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
public class RedirectOut extends AbstractStatement {

    private final Statement srcStatement;
    private final ResetableValueExpression toExpr;

    public RedirectOut(Statement srcStatement, ResetableValueExpression toExpr, int line, int column) {
        super(line, column);
        this.srcStatement = srcStatement;
        this.toExpr = toExpr;
    }

    public Object execute(final Context context) {

        final Out current;
        if ((current = context.getOut()).isByteStream()) {

            final FastByteArrayOutputStream out = new FastByteArrayOutputStream(128);

            StatementUtil.execute(srcStatement, context, new OutputStreamOut(out, (OutputStreamOut) current));
            StatementUtil.executeSetValue(toExpr, context, out.toByteArray());

        } else {

            final FastCharArrayWriter writer = new FastCharArrayWriter();

            StatementUtil.execute(srcStatement, context,
                    current instanceof WriterOut
                    ? new WriterOut(writer, (WriterOut) current)
                    : new WriterOut(writer, context.encoding, context.template.engine.getCoderFactory()));
            
            StatementUtil.executeSetValue(toExpr, context, writer.toCharArray());

        }
        return null;
    }
}
