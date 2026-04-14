/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.febit.wit.io.out;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.io.CodecFactory;
import org.febit.wit.io.Out;
import org.febit.wit.io.codec.Decoder;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;

@Accessors(fluent = true)
public class WriterOut implements Out {

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
            this.decoder.decode(bytes, offset, length, this.writer);
        } catch (IOException ex) {
            throw new ScriptEvaluateException(ex);
        }
    }

    @Override
    public void write(byte[] bytes) {
        try {
            this.decoder.decode(bytes, 0, bytes.length, this.writer);
        } catch (IOException ex) {
            throw new ScriptEvaluateException(ex);
        }
    }

    @Override
    public void write(char[] chars, int offset, int length) {
        try {
            this.writer.write(chars, offset, length);
        } catch (IOException ex) {
            throw new ScriptEvaluateException(ex);
        }
    }

    @Override
    public void write(char[] chars) {
        try {
            this.writer.write(chars);
        } catch (IOException ex) {
            throw new ScriptEvaluateException(ex);
        }
    }

    @Override
    public void write(String string, int offset, int length) {
        try {
            this.writer.write(string, offset, length);
        } catch (IOException ex) {
            throw new ScriptEvaluateException(ex);
        }
    }

    @Override
    public boolean preferBytes() {
        return false;
    }
}
