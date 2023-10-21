// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.text.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.InternedEncoding;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Statement;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public final class SimpleTextStatement implements Statement {

    private final char[] text;
    private final byte[] textBytes;
    private final InternedEncoding encoding;
    @Getter
    private final Position position;

    @Override
    public Object execute(final InternalContext context) {
        var out = context.getOut();
        if (out.preferBytes() && encoding == out.getEncoding()) {
            out.write(textBytes);
        } else {
            out.write(text);
        }
        return null;
    }
}
