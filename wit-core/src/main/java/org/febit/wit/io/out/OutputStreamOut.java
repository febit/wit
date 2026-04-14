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
import org.febit.wit.io.codec.Encoder;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

@Accessors(fluent = true)
public class OutputStreamOut implements Out {

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
            throw new ScriptEvaluateException(ex);
        }
    }

    @Override
    public void write(byte[] bytes) {
        try {
            this.output.write(bytes);
        } catch (IOException ex) {
            throw new ScriptEvaluateException(ex);
        }
    }

    @Override
    public void write(char[] chars, int offset, int length) {
        try {
            this.encoder.encode(chars, offset, length, this.output);
        } catch (IOException ex) {
            throw new ScriptEvaluateException(ex);
        }
    }

    @Override
    public void write(char[] chars) {
        write(chars, 0, chars.length);
    }

    @Override
    public void write(String string, int offset, int length) {
        try {
            this.encoder.encode(string, offset, length, this.output);
        } catch (IOException ex) {
            throw new ScriptEvaluateException(ex);
        }
    }

    @Override
    public boolean preferBytes() {
        return true;
    }
}
