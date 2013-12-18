// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.text.impl;

import webit.script.Context;
import webit.script.core.ast.AbstractStatement;
import webit.script.core.ast.Optimizable;

/**
 *
 * @author Zqq
 */
public final class ByteArrayTextStatement extends AbstractStatement implements Optimizable {

    private final byte[] bytes;

    public ByteArrayTextStatement(byte[] bytes, int line, int column) {
        super(line, column);
        this.bytes = bytes;
    }

    public Object execute(final Context context) {
        context.out(bytes);
        return null;
    }

    public ByteArrayTextStatement optimize() {
        return bytes != null && bytes.length > 0 ? this : null;
    }
}
