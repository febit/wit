// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.text.impl;

import webit.script.Context;
import webit.script.core.ast.Statement;

/**
 *
 * @author zqq90
 */
public final class SimpleTextStatement extends Statement {

    private final String encoding;
    private final char[] text;
    private final byte[] textBytes;

    public SimpleTextStatement(char[] chars, byte[] bytes, String encoding, int line, int column) {
        super(line, column);
        this.text = chars;
        this.encoding = encoding;
        this.textBytes = bytes;
    }

    @Override
    public Object execute(final Context context) {
        if (context.isByteStream && encoding == context.encoding) {
            context.outNotNull(textBytes);
        } else {
            context.outNotNull(text);
        }
        return null;
    }
}
