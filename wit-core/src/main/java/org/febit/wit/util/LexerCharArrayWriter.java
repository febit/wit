// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.util;

import java.io.CharArrayWriter;

public class LexerCharArrayWriter extends CharArrayWriter {

    public LexerCharArrayWriter() {
        this(256);
    }

    public LexerCharArrayWriter(int size) {
        super(size);
    }

    public void write(char cbuf[]) {
        write(cbuf, 0, cbuf.length);
    }

    public char[] toArrayOmitStartingLineSeparator() {
        final int size = this.count;
        if (size == 0) {
            return new char[0];
        }
        final int skip;
        final char[] source = this.buf;
        switch (source[0]) {
            case '\n':
                skip = 1;
                break;
            case '\r':
                if (size <= 1) {
                    return new char[0];
                }
                if (source[1] == '\n') {
                    if (size == 2) {
                        return new char[0];
                    }
                    skip = 2;
                } else {
                    skip = 1;
                }
                break;
            default:
                return toCharArray();
        }
        final char[] array = new char[size - skip];
        System.arraycopy(source, skip, array, 0, count - skip);
        return array;
    }

    public void trimRightAfterLastLineSeparator() {
        final char[] source = this.buf;
        int pos = this.count - 1;
        char c;
        // find unblank char pos 
        while (pos >= 0) {
            c = source[pos];
            if (c != ' ' && c != '\t') {
                break;
            }
            pos--;
        }
        if (pos < 0) {
            // no more chars, not find line separator
            return;
        }
        c = source[pos];
        if (c == '\n' || c == '\r') {
            // trim to line separator
            this.count = pos + 1;
        }
    }
}
