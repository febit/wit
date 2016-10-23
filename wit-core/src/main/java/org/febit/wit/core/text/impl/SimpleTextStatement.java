// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.text.impl;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.Statement;
import org.febit.wit.util.InternedEncoding;

/**
 *
 * @author zqq90
 */
public final class SimpleTextStatement extends Statement {

    private final InternedEncoding encoding;
    private final char[] text;
    private final byte[] textBytes;

    public SimpleTextStatement(char[] chars, byte[] bytes, InternedEncoding encoding, int line, int column) {
        super(line, column);
        this.text = chars;
        this.encoding = encoding;
        this.textBytes = bytes;
    }

    @Override
    public Object execute(final InternalContext context) {
        if (context.isByteStream && encoding == context.encoding) {
            context.outNotNull(textBytes);
        } else {
            context.outNotNull(text);
        }
        return null;
    }
}
