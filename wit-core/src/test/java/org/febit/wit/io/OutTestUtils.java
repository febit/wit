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
package org.febit.wit.io;

import lombok.experimental.UtilityClass;
import org.apache.commons.io.output.AppendableWriter;
import org.apache.commons.io.output.WriterOutputStream;
import org.febit.wit.io.codec.DefaultCodecFactory;
import org.febit.wit.io.out.OutputStreamOut;
import org.febit.wit.io.out.WriterOut;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

@UtilityClass
public class OutTestUtils {

    public static WriterOut wrapAsWriterOut(Appendable appendable) {
        var writer = new AppendableWriter<>(appendable);
        return new WriterOut(writer, StandardCharsets.UTF_8, new DefaultCodecFactory());
    }

    public static OutputStreamOut wrapAsOutputStreamOut(Appendable appendable) {
        var writer = new AppendableWriter<>(appendable);
        try {
            var output = WriterOutputStream.builder()
                    .setWriter(writer)
                    .setWriteImmediately(true)
                    .setCharset(StandardCharsets.UTF_8)
                    .get();
            return wrapAsOutputStreamOut(output);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static OutputStreamOut wrapAsOutputStreamOut(OutputStream output) {
        return new OutputStreamOut(output, StandardCharsets.UTF_8, new DefaultCodecFactory());
    }
}
