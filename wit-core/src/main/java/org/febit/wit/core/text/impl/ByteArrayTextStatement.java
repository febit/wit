// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.text.impl;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.Statement;

/**
 *
 * @author zqq90
 */
public final class ByteArrayTextStatement extends Statement {

    private final byte[] bytes;

    public ByteArrayTextStatement(byte[] bytes, int line, int column) {
        super(line, column);
        this.bytes = bytes;
    }

    @Override
    public Object execute(final InternalContext context) {
        context.outNotNull(bytes);
        return null;
    }
}
