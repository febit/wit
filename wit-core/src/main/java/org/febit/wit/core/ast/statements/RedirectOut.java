// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.statements;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.AssignableExpression;
import org.febit.wit.core.ast.Statement;
import org.febit.wit.io.impl.OutputStreamOut;
import org.febit.wit.io.impl.WriterOut;

/**
 *
 * @author zqq90
 */
public class RedirectOut extends Statement {

    private final Statement srcStatement;
    private final AssignableExpression toExpr;

    public RedirectOut(Statement srcStatement, AssignableExpression toExpr, int line, int column) {
        super(line, column);
        this.srcStatement = srcStatement;
        this.toExpr = toExpr;
    }

    @Override
    public Object execute(final InternalContext context) {
        if (context.isByteStream) {
            ByteArrayOutputStream out = new ByteArrayOutputStream(256);
            context.temporaryOut(new OutputStreamOut(out, context.getEngine()),
                    srcStatement::execute);
            toExpr.setValue(context, out.toByteArray());
        } else {
            CharArrayWriter writer = new CharArrayWriter(256);
            context.temporaryOut(new WriterOut(writer, context.getEngine()),
                    srcStatement::execute);
            toExpr.setValue(context, writer.toCharArray());
        }
        return null;
    }
}
