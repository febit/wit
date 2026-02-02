// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.io.codec;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor(staticName = "of")
final class Buffers {

    private static final int DEFAULT_SIZE = 1024;

    private final int size;

    private char @Nullable [] chars;
    private byte @Nullable [] bytes;

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
