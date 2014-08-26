// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.text.impl;

import java.io.IOException;
import webit.script.Engine;
import webit.script.Initable;
import webit.script.Template;
import webit.script.core.ast.Statement;
import webit.script.core.text.TextStatementFactory;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.io.charset.CoderFactory;
import webit.script.io.charset.Encoder;
import webit.script.util.FastByteArrayOutputStream;

/**
 *
 * @author zqq90
 */
public class ByteArrayTextStatementFactory implements TextStatementFactory, Initable {

    protected String encoding;
    protected CoderFactory coderFactory;
    protected final ThreadLocal<Encoder> encoders = new ThreadLocal<Encoder>();
    protected final ThreadLocal<FastByteArrayOutputStream> outputs = new ThreadLocal<FastByteArrayOutputStream>();

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

    protected byte[] getBytes(char[] text) {
        try {
            final FastByteArrayOutputStream outputStream = outputs.get();
            encoders.get().write(text, 0, text.length, outputStream);
            final byte[] bytes = outputStream.toByteArray();
            outputStream.reset();
            return bytes;
        } catch (IOException ex) {
            throw new ScriptRuntimeException(ex);
        }
    }

    public Statement getTextStatement(Template template, char[] text, int line, int column) {
        return new ByteArrayTextStatement(getBytes(text), line, column);
    }
}
