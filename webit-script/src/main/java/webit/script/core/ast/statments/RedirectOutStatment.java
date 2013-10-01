// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.ResetableValue;
import webit.script.core.ast.ResetableValueExpression;
import webit.script.core.ast.Statment;
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
public class RedirectOutStatment extends AbstractStatment {

    private final Statment srcStatment;
    private final ResetableValueExpression toExpr;

    public RedirectOutStatment(Statment srcStatment, ResetableValueExpression toExpr, int line, int column) {
        super(line, column);
        this.srcStatment = srcStatment;
        this.toExpr = toExpr;
    }

    public Object execute(final Context context) {

        final Out current = context.getOut();
        if (current instanceof OutputStreamOut) {

            final FastByteArrayOutputStream out = new FastByteArrayOutputStream(128);

            StatmentUtil.execute(srcStatment, context, new OutputStreamOut(out, (OutputStreamOut) current));
            StatmentUtil.executeSetValue(toExpr, context, out.toByteArray());
            
        } else if (current instanceof WriterOut) {

            final FastCharArrayWriter writer = new FastCharArrayWriter();

            StatmentUtil.execute(srcStatment, context, new WriterOut(writer, (WriterOut) current));
            StatmentUtil.executeSetValue(toExpr, context, writer.toCharArray());
            
        } else {

            final FastCharArrayWriter writer = new FastCharArrayWriter();

            StatmentUtil.execute(srcStatment, context, new WriterOut(writer, context.encoding, context.template.engine.getCoderFactory()));
            StatmentUtil.executeSetValue(toExpr, context, writer.toCharArray());
            
        }
        return null;
    }
}
