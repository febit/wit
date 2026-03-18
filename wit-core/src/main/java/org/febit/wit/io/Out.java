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

import java.nio.charset.Charset;

public interface Out {

    boolean preferBytes();

    Charset charset();

    void write(byte[] bytes, int offset, int length);

    default void write(byte[] bytes) {
        write(bytes, 0, bytes.length);
    }

    void write(char[] chars, int offset, int length);

    default void write(char[] chars) {
        write(chars, 0, chars.length);
    }

    void write(String string, int offset, int length);

    default void write(String string) {
        write(string, 0, string.length());
    }

}
