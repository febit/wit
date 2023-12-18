// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.io.impl;

import lombok.val;
import org.febit.wit.Engine;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.io.Buffers;
import org.febit.wit.io.charset.CoderFactory;
import org.febit.wit.io.charset.Decoder;
import org.febit.wit.lang.InternedEncoding;
import org.febit.wit.lang.Out;

import java.io.IOException;
import java.io.Writer;

/**
 * @author zqq90
 */
public final class WriterOut implements Out {

    private final Writer writer;
    private final InternedEncoding encoding;
    private final Decoder decoder;
    private final Buffers buffers;

    public WriterOut(Writer writer, InternedEncoding encoding, Engine engine) {
        this(writer, encoding != null ? encoding : engine.getEncoding(), engine.getCoderFactory());
    }

    public WriterOut(Writer writer, Engine engine) {
        this(writer, engine.getEncoding(), engine.getCoderFactory());
    }

    public WriterOut(Writer writer, InternedEncoding encoding, CoderFactory coderFactory) {
        this.writer = writer;
        this.encoding = encoding;
        this.buffers = Buffers.getMiniPeers();
        this.decoder = coderFactory.newDecoder(encoding, this.buffers);
    }

    @Override
    public void write(final byte[] bytes, final int offset, final int length) {
        try {
            this.decoder.write(bytes, offset, length, this.writer);
        } catch (IOException ex) {
            throw new ScriptRuntimeException(ex);
        }
    }

    @Override
    public void write(final byte[] bytes) {
        try {
            this.decoder.write(bytes, 0, bytes.length, this.writer);
        } catch (IOException ex) {
            throw new ScriptRuntimeException(ex);
        }
    }

    @Override
    public void write(final char[] chars, final int offset, final int length) {
        try {
            this.writer.write(chars, offset, length);
        } catch (IOException ex) {
            throw new ScriptRuntimeException(ex);
        }
    }

    @Override
    public void write(final char[] chars) {
        try {
            this.writer.write(chars);
        } catch (IOException ex) {
            throw new ScriptRuntimeException(ex);
        }
    }

    @Override
    public void write(final String string, final int offset, final int length) {
        try {
            val chars = this.buffers.getChars(length);
            string.getChars(offset, offset + length, chars, 0);
            this.writer.write(chars, 0, length);
        } catch (IOException ex) {
            throw new ScriptRuntimeException(ex);
        }
    }

    @Override
    public InternedEncoding getEncoding() {
        return this.encoding;
    }

    @Override
    public boolean preferBytes() {
        return false;
    }
}
