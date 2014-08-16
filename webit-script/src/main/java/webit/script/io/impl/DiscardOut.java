// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.io.impl;

import webit.script.io.Out;
import webit.script.util.EncodingPool;

/**
 *
 * @author zqq90
 */
public class DiscardOut implements Out {

    private final String encoding;
    private final boolean isByteStream;

    public DiscardOut() {
        this(EncodingPool.UTF_8, false);
    }

    public DiscardOut(String encoding, boolean isByteStream) {
        this.encoding = encoding;
        this.isByteStream = isByteStream;
    }

    public void write(byte[] bytes, int offset, int length) {
    }

    public void write(byte[] bytes) {
    }

    public void write(char[] chars, int offset, int length) {
    }

    public void write(char[] chars) {
    }

    public void write(String string, int offset, int length) {
    }

    public void write(String string) {
    }

    public String getEncoding() {
        return encoding;
    }

    public boolean isByteStream() {
        return isByteStream;
    }

}
