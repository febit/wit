// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.io;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.febit.wit.Out;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.io.codec.CodecFactory;
import org.febit.wit.io.codec.Decoder;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;

@Accessors(fluent = true)
public final class WriterOut implements Out {

    private final Writer writer;
    @Getter
    private final Charset charset;
    private final Decoder decoder;

    public WriterOut(Writer writer, Charset charset, CodecFactory codecFactory) {
        this.writer = writer;
        this.charset = charset;
        this.decoder = codecFactory.decoder(charset);
    }

    @Override
    public void write(byte[] bytes, int offset, int length) {
        try {
            this.decoder.write(bytes, offset, length, this.writer);
        } catch (IOException ex) {
            throw new ScriptRuntimeException(ex);
        }
    }

    @Override
    public void write(byte[] bytes) {
        try {
            this.decoder.write(bytes, 0, bytes.length, this.writer);
        } catch (IOException ex) {
            throw new ScriptRuntimeException(ex);
        }
    }

    @Override
    public void write(char[] chars, int offset, int length) {
        try {
            this.writer.write(chars, offset, length);
        } catch (IOException ex) {
            throw new ScriptRuntimeException(ex);
        }
    }

    @Override
    public void write(char[] chars) {
        try {
            this.writer.write(chars);
        } catch (IOException ex) {
            throw new ScriptRuntimeException(ex);
        }
    }

    @Override
    public void write(String string, int offset, int length) {
        try {
            this.writer.write(string, offset, length);
        } catch (IOException ex) {
            throw new ScriptRuntimeException(ex);
        }
    }

    @Override
    public boolean preferBytes() {
        return false;
    }
}
