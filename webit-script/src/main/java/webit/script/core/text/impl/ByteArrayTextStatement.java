// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.text.impl;

import webit.script.Context;
import webit.script.core.ast.AbstractStatement;

/**
 *
 * @author Zqq
 */
public final class ByteArrayTextStatement extends AbstractStatement {

    private final byte[] bytes;

    public ByteArrayTextStatement(byte[] bytes, int line, int column) {
        super(line, column);
        this.bytes = bytes;
    }

    public Object execute(final Context context) {
        context.outNotNull(bytes);
        return null;
    }
}
