// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.io.impl;

import java.io.IOException;
import java.io.Writer;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.io.Out;

/**
 *
 * @author Zqq
 */
public final class WriterOut implements Out {

    private final Writer writer;
    private final String encoding;

    public WriterOut(Writer writer, String encoding) {
        this.writer = writer;
        this.encoding = encoding;
    }

    public void write(byte[] bytes, int offset, int length) {
        try {
            write(new String(bytes, offset, length, encoding));
        } catch (IOException ex) {
            throw new ScriptRuntimeException(ex);
        }
    }

    public void write(byte[] bytes) {
        try {
            write(new String(bytes, encoding));
        } catch (IOException ex) {
            throw new ScriptRuntimeException(ex);
        }
    }

    public void write(char[] chars, int offset, int length) {
        try {
            writer.write(chars, offset, length);
        } catch (IOException ex) {
            throw new ScriptRuntimeException(ex);
        }
    }

    public void write(char[] chars) {
        try {
            writer.write(chars);
        } catch (IOException ex) {
            throw new ScriptRuntimeException(ex);
        }
    }

    public void write(String string, int offset, int length) {
        try {
            char[] chars = new char[length];
            string.getChars(offset, offset + length, new char[length], 0);
            writer.write(chars);
        } catch (IOException ex) {
            throw new ScriptRuntimeException(ex);
        }
    }

    public void write(String string) {
        try {
            writer.write(string.toCharArray());
        } catch (IOException ex) {
            throw new ScriptRuntimeException(ex);
        }
    }

    public String getEncoding() {
        return encoding;
    }
}
