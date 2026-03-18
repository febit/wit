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

import org.febit.wit.io.Out;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public record DiscardOut(
        Charset charset,
        boolean preferBytes
) implements Out {

    public static final DiscardOut INSTANCE = new DiscardOut();

    public static DiscardOut get() {
        return INSTANCE;
    }

    public DiscardOut() {
        this(StandardCharsets.UTF_8, false);
    }

    @Override
    public void write(byte[] bytes, int offset, int length) {
        // Do nothing
    }

    @Override
    public void write(byte[] bytes) {
        // Do nothing
    }

    @Override
    public void write(char[] chars, int offset, int length) {
        // Do nothing
    }

    @Override
    public void write(char[] chars) {
        // Do nothing
    }

    @Override
    public void write(String string, int offset, int length) {
        // Do nothing
    }

    @Override
    public void write(String string) {
        // Do nothing
    }

}
