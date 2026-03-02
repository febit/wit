// Copyright (c) 2013-present, febit.org. All Rights Reserved.
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
