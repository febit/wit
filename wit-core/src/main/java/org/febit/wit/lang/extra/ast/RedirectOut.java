// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.extra.ast;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.InternalContext;
import org.febit.wit.io.OutputStreamOut;
import org.febit.wit.io.WriterOut;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.AssignableExpression;
import org.febit.wit.lang.ast.Statement;
import org.jspecify.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;

@Accessors(fluent = true)
@RequiredArgsConstructor
public class RedirectOut implements Statement {

    private final Statement srcStatement;
    private final AssignableExpression target;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        var out = context.out();
        var engine = context.engine();
        var charset = out.charset();
        var codecFactory = engine.codecFactory();

        if (out.preferBytes()) {
            var buffer = new ByteArrayOutputStream(256);
            context.redirectOut(new OutputStreamOut(buffer, charset, codecFactory),
                    srcStatement::execute);
            target.setValue(context, buffer.toByteArray());
        } else {
            var buffer = new CharArrayWriter(256);
            context.redirectOut(new WriterOut(buffer, charset, codecFactory),
                    srcStatement::execute);
            target.setValue(context, buffer.toCharArray());
        }
        return null;
    }
}
