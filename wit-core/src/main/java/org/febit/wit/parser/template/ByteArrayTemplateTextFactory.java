// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.parser.template;

import org.febit.wit.Script;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.io.codec.Encoder;
import org.febit.wit.parser.TemplateTextFactory;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.template.ByteArrayTemplateText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ByteArrayTemplateTextFactory implements TemplateTextFactory {

    private final ThreadLocal<Encoder> encoders = new ThreadLocal<>();
    private final ThreadLocal<ByteArrayOutputStream> outputs = new ThreadLocal<>();

    @Override
    public void onParserStarted(Script script) {
        var wit = script.engine();
        encoders.set(wit.codecFactory().encoder(wit.charset()));
        outputs.set(new ByteArrayOutputStream(512));
    }

    @Override
    public void onParserCompleted(Script script) {
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
            throw new ScriptEvaluateException(ex);
        }
    }

    @Override
    public Statement create(Script script, char[] text, Position position) {
        return new ByteArrayTemplateText(encode(text), position);
    }
}
