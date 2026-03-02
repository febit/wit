// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import java.io.CharArrayWriter;
import java.util.Arrays;

public class LexerCharsBuffer extends CharArrayWriter {

    public LexerCharsBuffer(int size) {
        super(size);
    }

    @Override
    public void write(char[] source) {
        write(source, 0, source.length);
    }

    public char[] toCharsWithoutLeadingLineEnding() {
        var size = this.count;
        if (size == 0) {
            return new char[0];
        }
        var src = this.buf;
        int skip;
        switch (src[0]) {
            case '\n' -> skip = 1;
            case '\r' -> {
                if (size <= 1) {
                    return new char[0];
                }
                if (src[1] == '\n') {
                    if (size == 2) {
                        return new char[0];
                    }
                    skip = 2;
                } else {
                    skip = 1;
                }
            }
            default -> {
                return toCharArray();
            }
        }
        return Arrays.copyOfRange(src, skip, size);
    }

    public void trimTrailingBlankLine() {
        var source = this.buf;
        int pos = this.count - 1;
        char c;

        // Backward scan for trailing blanks
        while (pos >= 0) {
            c = source[pos];
            if (c != ' ' && c != '\t') {
                break;
            }
            pos--;
        }

        // Abort trimming, if no more chars.
        if (pos < 0) {
            return;
        }

        c = source[pos];

        // Abort trimming, if last char is not CR/LF.
        if (c != '\n' && c != '\r') {
            return;
        }

        // trim last CR/LF.
        this.count = pos + 1;
    }
}
