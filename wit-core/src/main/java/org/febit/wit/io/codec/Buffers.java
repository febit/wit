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
package org.febit.wit.io.codec;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Accessors(fluent = true)
final class Buffers {

    private static final int DEFAULT_SIZE = 1024;

    /**
     * If the buffer size is too small, unexpected exceptions may occur,
     * such as {@code BufferOverflowException} or {@code MalformedInputException}.
     */
    private static final int MIN_SIZE = 16;

    @Getter
    private final int size;

    private char @Nullable [] chars;
    private byte @Nullable [] bytes;

    public static Buffers of(int size) {
        if (size < MIN_SIZE) {
            log.warn("Buffer size {} is too small, use {} instead", size, MIN_SIZE);
            size = MIN_SIZE;
        }
        return new Buffers(size);
    }

    public static Buffers of() {
        return of(DEFAULT_SIZE);
    }

    public char[] chars() {
        var buf = this.chars;
        if (buf == null) {
            buf = new char[size];
            this.chars = buf;
        }
        return buf;
    }

    public byte[] bytes() {
        var buf = this.bytes;
        if (buf == null) {
            buf = new byte[size];
            this.bytes = buf;
        }
        return buf;
    }
}
