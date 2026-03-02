// Copyright (c) 2013-present, febit.org. All Rights Reserved.
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
