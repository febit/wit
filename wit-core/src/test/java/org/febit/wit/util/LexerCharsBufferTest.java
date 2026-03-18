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
package org.febit.wit.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LexerCharsBufferTest {

    @Test
    void trimTest() {
        LexerCharsBuffer buffer;

        buffer = new LexerCharsBuffer(3);
        buffer.append("\t\t\t")
                .append("\t\t\t");
        buffer.trimTrailingBlankLine();
        assertEquals(6, buffer.size());

        buffer = new LexerCharsBuffer(3);
        buffer.append("abc")
                .append("def");
        buffer.trimTrailingBlankLine();
        assertEquals(6, buffer.size());

        buffer = new LexerCharsBuffer(3);
        buffer.append("\n\t\t")
                .append("\t\t\t");
        buffer.trimTrailingBlankLine();
        assertEquals(1, buffer.size());

        buffer = new LexerCharsBuffer(3);
        buffer.append("\r\n\t")
                .append("\t\t\t");
        buffer.trimTrailingBlankLine();
        assertEquals(2, buffer.size());

        buffer = new LexerCharsBuffer(3);
        buffer.append("\t\n\n")
                .append("\t\t\t");
        buffer.trimTrailingBlankLine();
        assertEquals("\t\n\n", buffer.toString());
    }

    @Test
    void omitStartingLineSeparator() {
        char[] chars;
        LexerCharsBuffer buffer;

        buffer = new LexerCharsBuffer(3);
        buffer.append("\r\n\t");
        chars = buffer.toCharsWithoutLeadingLineEnding();
        assertEquals(1, chars.length);
        assertEquals('\t', chars[0]);

        buffer = new LexerCharsBuffer(3);
        buffer.append("\t\n\n")
                .append("\t\t\t");
        chars = buffer.toCharsWithoutLeadingLineEnding();
        assertEquals(6, chars.length);
        assertEquals('\t', chars[0]);
        assertEquals('\t', chars[5]);

        buffer = new LexerCharsBuffer(3);
        buffer.append("\n\n\t")
                .append("\t\t\t");
        chars = buffer.toCharsWithoutLeadingLineEnding();
        assertEquals(5, chars.length);
        assertEquals('\n', chars[0]);
        assertEquals('\t', chars[4]);

        buffer = new LexerCharsBuffer(3);
        buffer.append("\r\n\t")
                .append("\t\t\t");
        chars = buffer.toCharsWithoutLeadingLineEnding();
        assertEquals(4, chars.length);
        assertEquals('\t', chars[0]);
        assertEquals('\t', chars[3]);

        buffer = new LexerCharsBuffer(3);
        buffer.append("\r\n\n")
                .append("\t\t\t");
        buffer.trimTrailingBlankLine();
        chars = buffer.toCharsWithoutLeadingLineEnding();
        assertEquals(1, chars.length);
        assertEquals('\n', chars[0]);

        buffer = new LexerCharsBuffer(3);
        buffer.append("\r\n\t")
                .append("\t\t\t");

        buffer.trimTrailingBlankLine();
        chars = buffer.toCharsWithoutLeadingLineEnding();
        assertEquals(0, chars.length);
    }
}
