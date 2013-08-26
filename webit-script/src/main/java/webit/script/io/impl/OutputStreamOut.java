// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.io.impl;

import java.io.IOException;
import java.io.OutputStream;
import jodd.util.StringUtil;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.io.Out;

/**
 *
 * @author Zqq
 */
public final class OutputStreamOut implements Out {

    private final OutputStream outputStream;
    private final String encoding;

    public OutputStreamOut(OutputStream outputStream, String encoding) {
        this.outputStream = outputStream;
        this.encoding = encoding;
    }

    public void write(byte[] bytes, int offset, int length) {
        try {
            outputStream.write(bytes, offset, length);
        } catch (IOException ex) {
            throw new ScriptRuntimeException(ex);
        }
    }

    public void write(byte[] bytes) {
        try {
            outputStream.write(bytes);
        } catch (IOException ex) {
            throw new ScriptRuntimeException(ex);
        }
    }

    public void write(char[] chars, int offset, int length) {
        write(new String(chars, offset, length));
    }

    public void write(char[] chars) {
        write(new String(chars));
    }

    public void write(String string, int offset, int length) {
        write(string.substring(offset, offset + length));
    }

    public void write(String string) {
        try {
            outputStream.write(string.getBytes(encoding));
        } catch (IOException ex) {
            throw new ScriptRuntimeException(ex);
        }
    }

    public String getEncoding() {
        return encoding;
    }
}
