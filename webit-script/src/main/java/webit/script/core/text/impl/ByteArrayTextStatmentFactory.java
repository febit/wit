// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.text.impl;

import java.io.IOException;
import webit.script.Engine;
import webit.script.Initable;
import webit.script.Template;
import webit.script.core.ast.Statment;
import webit.script.core.text.TextStatmentFactory;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.io.charset.CoderFactory;
import webit.script.io.charset.Encoder;
import webit.script.util.FastByteArrayOutputStream;

/**
 *
 * @author Zqq
 */
public class ByteArrayTextStatmentFactory implements TextStatmentFactory, Initable {

    private String encoding;
    private CoderFactory coderFactory;
    private final ThreadLocal<Encoder> encoders = new ThreadLocal<Encoder>();
    private final ThreadLocal<FastByteArrayOutputStream> outputs = new ThreadLocal<FastByteArrayOutputStream>();

    public void init(Engine engine) {
        encoding = engine.getEncoding();
        coderFactory = engine.getCoderFactory();
    }

    public void startTemplateParser(Template template) {
        encoders.set(coderFactory.newEncoder(encoding));
        outputs.set(new FastByteArrayOutputStream(512));
    }

    public void finishTemplateParser(Template template) {
        encoders.remove();
        outputs.remove();
    }

    private byte[] getBytes(char[] text) {
        if (text != null) {
            try {
                final FastByteArrayOutputStream outputStream;
                encoders.get().write(text, 0, text.length, outputStream = outputs.get());
                final byte[] bytes = outputStream.toByteArray();
                outputStream.reset();
                return bytes;
            } catch (IOException ex) {
                throw new ScriptRuntimeException(ex);
            }
        }
        return null;
    }

    public Statment getTextStatment(Template template, char[] text, int line, int column) {
        return new ByteArrayTextStatment(text != null ? getBytes(text) : null, line, column);
    }
}
