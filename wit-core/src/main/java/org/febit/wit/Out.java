// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit;

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
