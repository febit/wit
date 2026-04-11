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
package org.febit.wit.parser.support;

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
