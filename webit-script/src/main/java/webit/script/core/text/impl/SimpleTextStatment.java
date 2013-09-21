// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.text.impl;

import java.io.UnsupportedEncodingException;
import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Optimizable;

/**
 *
 * @author Zqq
 */
public final class SimpleTextStatment extends AbstractStatment implements Optimizable {

    private final char[] text;
    private final String encoding;
    private final byte[] textBytes;

    public SimpleTextStatment(String text, String encoding, int line, int column) {
        super(line, column);
        this.text = text.toCharArray();
        this.encoding = encoding;
        byte[] bytes;
        try {
            bytes = text.getBytes(encoding);
        } catch (UnsupportedEncodingException ex) {
            //Note:ignore
            bytes = text.getBytes();
        }
        this.textBytes = bytes;
    }

    public Object execute(final Context context) {
        if (context.isByteStream && (encoding == context.encoding || encoding.equals(context.encoding))) {
            context.out(textBytes);
        } else {
            context.out(text);
        }
        return null;
    }

    public SimpleTextStatment optimize() {
        return text != null && text.length > 0 ? this : null;
    }
}
