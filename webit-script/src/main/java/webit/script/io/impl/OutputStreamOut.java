// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.io.impl;

import java.io.IOException;
import java.io.OutputStream;
import webit.script.Engine;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.io.Out;
import webit.script.io.charset.CoderFactory;
import webit.script.io.charset.Encoder;

/**
 *
 * @author Zqq
 */
public final class OutputStreamOut implements Out {

    private final OutputStream outputStream;
    private final String encoding;
    private final Encoder encoder;

    public OutputStreamOut(OutputStream outputStream, String encoding, Encoder encoder) {
        this.outputStream = outputStream;
        this.encoding = encoding;
        this.encoder = encoder;
    }

    public OutputStreamOut(OutputStream outputStream, OutputStreamOut out) {
        this(outputStream, out.encoding, out.encoder);
    }

    public OutputStreamOut(OutputStream outputStream, Engine engine) {
        this(outputStream, engine.getEncoding(), engine.getCoderFactory());
    }
    
    public OutputStreamOut(OutputStream outputStream, String encoding, Engine engine) {
        this(outputStream, encoding, engine.getCoderFactory());
    }

    public OutputStreamOut(OutputStream outputStream, String encoding, CoderFactory coderFactory) {
        this(outputStream, encoding, coderFactory.newEncoder(encoding));
    }

    public void write(final byte[] bytes, final int offset, final int length) {
        try {
            this.outputStream.write(bytes, offset, length);
        } catch (IOException ex) {
            throw new ScriptRuntimeException(ex);
        }
    }

    public void write(final byte[] bytes) {
        try {
            this.outputStream.write(bytes);
        } catch (IOException ex) {
            throw new ScriptRuntimeException(ex);
        }
    }

    public void write(final char[] chars, final int offset, final int length) {
        try {
            this.encoder.write(chars, offset, length, this.outputStream);
        } catch (IOException ex) {
            throw new ScriptRuntimeException(ex);
        }
    }

    public void write(final char[] chars) {
        write(chars, 0, chars.length);
    }

    public void write(final String string, final int offset, final int length) {
        try {
            this.encoder.write(string, offset, length, this.outputStream);
        } catch (IOException ex) {
            throw new ScriptRuntimeException(ex);
        }
    }

    public void write(final String string) {
        try {
            this.encoder.write(string, 0, string.length(), this.outputStream);
        } catch (IOException ex) {
            throw new ScriptRuntimeException(ex);
        }
    }

    public String getEncoding() {
        return this.encoding;
    }

    public boolean isByteStream() {
        return true;
    }
}
