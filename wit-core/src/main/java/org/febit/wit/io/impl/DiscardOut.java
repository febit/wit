// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.io.impl;

import lombok.RequiredArgsConstructor;
import org.febit.wit.lang.InternedEncoding;
import org.febit.wit.lang.Out;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public class DiscardOut implements Out {

    public static final DiscardOut INSTANCE = new DiscardOut();

    private final InternedEncoding encoding;
    private final boolean preferBytes;

    public DiscardOut() {
        this("UTF-8", false);
    }

    public DiscardOut(String encoding, boolean preferBytes) {
        this(InternedEncoding.intern(encoding), preferBytes);
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

    @Override
    public InternedEncoding getEncoding() {
        return encoding;
    }

    @Override
    public boolean preferBytes() {
        return preferBytes;
    }

}
