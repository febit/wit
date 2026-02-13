// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.text;

import org.febit.wit.Template;
import org.febit.wit.core.TextStatementFactory;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.io.codec.Encoder;
import org.febit.wit.runtime.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.text.ByteArrayTextStatement;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ByteArrayTextStatementFactory implements TextStatementFactory {

    protected final ThreadLocal<Encoder> encoders = new ThreadLocal<>();
    protected final ThreadLocal<ByteArrayOutputStream> outputs = new ThreadLocal<>();

    @Override
    public void onParserStarted(Template template) {
        var engine = template.engine();
        encoders.set(engine.codecFactory().encoder(engine.charset()));
        outputs.set(new ByteArrayOutputStream(512));
    }

    @Override
    public void onParserCompleted(Template template) {
        encoders.remove();
        outputs.remove();
    }

    protected byte[] encode(char[] text) {
        try {
            var out = outputs.get();
            encoders.get().write(text, 0, text.length, out);
            var bytes = out.toByteArray();
            out.reset();
            return bytes;
        } catch (IOException ex) {
            throw new ScriptRuntimeException(ex);
        }
    }

    @Override
    public Statement create(Template template, char[] text, Position position) {
        return new ByteArrayTextStatement(encode(text), position);
    }
}
