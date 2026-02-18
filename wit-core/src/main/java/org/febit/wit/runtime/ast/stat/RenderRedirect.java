// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.stat;

import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AssignableExpression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.jspecify.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;

public record RenderRedirect(
        Statement body,
        AssignableExpression sink,
        Position position
) implements Statement {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        if (context.out().preferBytes()) {
            var buffer = new ByteArrayOutputStream(256);
            context.redirect(buffer, body::execute);
            sink.set(context, buffer.toByteArray());
        } else {
            var buffer = new CharArrayWriter(256);
            context.redirect(buffer, body::execute);
            sink.set(context, buffer.toCharArray());
        }
        return null;
    }
}
