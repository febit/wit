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

    private final String text;
    private final String encoding;
    private final byte[] textBytes;

    public SimpleTextStatment(String text, String encoding, int line, int column) {
        super(line, column);
        this.text = text;
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

    @Override
    public void execute(Context context) {
        if (context.encoding.equals(encoding)) {
            context.out(textBytes);
        } else {
            context.out(text);
        }
    }

    public SimpleTextStatment optimize() {
        if (text != null && text.length() > 0) {
            return this;
        }
        return null;
    }
}
