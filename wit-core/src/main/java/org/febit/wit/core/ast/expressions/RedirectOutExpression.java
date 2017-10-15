// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.expressions;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.AssignableExpression;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.io.Out;
import org.febit.wit.io.impl.OutputStreamOut;
import org.febit.wit.io.impl.WriterOut;
import org.febit.wit.util.ByteArrayOutputStream;
import org.febit.wit.util.CharArrayWriter;

/**
 *
 * @author zqq90
 */
public class RedirectOutExpression extends Expression {

    private final Expression srcExpr;
    private final AssignableExpression toExpr;

    public RedirectOutExpression(Expression srcExpr, AssignableExpression toExpr, int line, int column) {
        super(line, column);
        this.srcExpr = srcExpr;
        this.toExpr = toExpr;
    }

    @Override
    public Object execute(final InternalContext context) {

        final Out current = context.out;
        final Object result;
        if (current.isByteStream()) {
            ByteArrayOutputStream out = new ByteArrayOutputStream(256);
            context.out = new OutputStreamOut(out, (OutputStreamOut) current);
            result = srcExpr.execute(context);
            toExpr.setValue(context, out.toArray());
        } else {
            CharArrayWriter writer = new CharArrayWriter(256);
            context.out = current instanceof WriterOut
                    ? new WriterOut(writer, (WriterOut) current)
                    : new WriterOut(writer, context.encoding, context.template.engine.getCoderFactory());
            result = srcExpr.execute(context);
            toExpr.setValue(context, writer.toArray());
        }
        context.out = current;
        return result;
    }
}
