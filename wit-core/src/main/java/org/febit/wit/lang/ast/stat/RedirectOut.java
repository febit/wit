// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.io.impl.OutputStreamOut;
import org.febit.wit.io.impl.WriterOut;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.AssignableExpression;
import org.febit.wit.lang.ast.Statement;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public class RedirectOut implements Statement {

    private final Statement srcStatement;
    private final AssignableExpression target;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(final InternalContext context) {
        if (context.preferBytes) {
            ByteArrayOutputStream out = new ByteArrayOutputStream(256);
            context.temporaryOut(new OutputStreamOut(out, context.encoding, context.getEngine()),
                    srcStatement::execute);
            target.setValue(context, out.toByteArray());
        } else {
            CharArrayWriter writer = new CharArrayWriter(256);
            context.temporaryOut(new WriterOut(writer, context.encoding, context.getEngine()),
                    srcStatement::execute);
            target.setValue(context, writer.toCharArray());
        }
        return null;
    }
}
