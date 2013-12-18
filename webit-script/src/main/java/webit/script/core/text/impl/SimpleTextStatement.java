// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.text.impl;

import webit.script.Context;
import webit.script.core.ast.AbstractStatement;
import webit.script.core.ast.Optimizable;

/**
 *
 * @author Zqq
 */
public final class SimpleTextStatement extends AbstractStatement implements Optimizable {

    private final char[] text;
    private final String encoding;
    private final byte[] textBytes;

    public SimpleTextStatement(char[] chars, byte[] bytes, String encoding, int line, int column) {
        super(line, column);
        this.text = chars;
        this.encoding = encoding;
        this.textBytes = bytes;
    }

    public Object execute(final Context context) {
        if (context.isByteStream && encoding == context.encoding) {
            context.out(textBytes);
            return null;
        } else {
            context.out(text);
        }
        return null;
    }

    public SimpleTextStatement optimize() {
        return text != null && text.length > 0 ? this : null;
    }
}
