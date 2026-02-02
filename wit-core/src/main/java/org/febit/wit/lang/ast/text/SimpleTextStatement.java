// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.text;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Statement;
import org.jspecify.annotations.Nullable;

import java.nio.charset.Charset;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class SimpleTextStatement implements Statement {

    private final char[] chars;
    private final byte[] encoded;
    private final Charset charset;
    @Getter
    private final Position position;

    @Nullable
    @Override
    public Object execute(InternalContext context) {
        var out = context.out();
        if (out.preferBytes()
                && charset.equals(out.charset())) {
            out.write(encoded);
        } else {
            out.write(chars);
        }
        return null;
    }
}
