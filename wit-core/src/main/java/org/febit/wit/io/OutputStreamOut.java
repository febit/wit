// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.io;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.io.codec.CodecFactory;
import org.febit.wit.io.codec.Encoder;
import org.febit.wit.lang.Out;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

@Accessors(fluent = true)
public final class OutputStreamOut implements Out {

    private final OutputStream output;
    private final Encoder encoder;
    @Getter
    private final Charset charset;

    public OutputStreamOut(OutputStream output, Charset charset, CodecFactory codecFactory) {
        this.output = output;
        this.charset = charset;
        this.encoder = codecFactory.encoder(charset);
    }

    @Override
    public void write(byte[] bytes, int offset, int length) {
        try {
            this.output.write(bytes, offset, length);
        } catch (IOException ex) {
            throw new ScriptRuntimeException(ex);
        }
    }

    @Override
    public void write(byte[] bytes) {
        try {
            this.output.write(bytes);
        } catch (IOException ex) {
            throw new ScriptRuntimeException(ex);
        }
    }

    @Override
    public void write(char[] chars, int offset, int length) {
        try {
            this.encoder.write(chars, offset, length, this.output);
        } catch (IOException ex) {
            throw new ScriptRuntimeException(ex);
        }
    }

    @Override
    public void write(char[] chars) {
        write(chars, 0, chars.length);
    }

    @Override
    public void write(String string, int offset, int length) {
        try {
            this.encoder.write(string, offset, length, this.output);
        } catch (IOException ex) {
            throw new ScriptRuntimeException(ex);
        }
    }

    @Override
    public boolean preferBytes() {
        return true;
    }
}
