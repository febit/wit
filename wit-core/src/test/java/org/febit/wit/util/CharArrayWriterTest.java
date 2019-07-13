// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author zqq90
 */
class CharArrayWriterTest {

    @Test
    void trimTest() {
        LexerCharArrayWriter buffer;

        //
        buffer = new LexerCharArrayWriter(3);
        buffer.append("\t\t\t")
                .append("\t\t\t");
        buffer.trimRightAfterLastLineSeparator();
        assertEquals(6, buffer.size());

        //
        buffer = new LexerCharArrayWriter(3);
        buffer.append("abc")
                .append("def");
        buffer.trimRightAfterLastLineSeparator();
        assertEquals(6, buffer.size());

        //
        buffer = new LexerCharArrayWriter(3);
        buffer.append("\n\t\t")
                .append("\t\t\t");
        buffer.trimRightAfterLastLineSeparator();
        assertEquals(1, buffer.size());

        //
        buffer = new LexerCharArrayWriter(3);
        buffer.append("\r\n\t")
                .append("\t\t\t");
        buffer.trimRightAfterLastLineSeparator();
        assertEquals(2, buffer.size());

        //
        buffer = new LexerCharArrayWriter(3);
        buffer.append("\t\n\n")
                .append("\t\t\t");
        buffer.trimRightAfterLastLineSeparator();
        assertEquals("\t\n\n", buffer.toString());
        //

        char[] chars;

        buffer = new LexerCharArrayWriter(3);
        buffer.append("\r\n\t");

        chars = buffer.toArrayOmitStartingLineSeparator();
        assertEquals(1, chars.length);
        assertEquals('\t', chars[0]);

        buffer = new LexerCharArrayWriter(3);
        buffer.append("\t\n\n")
                .append("\t\t\t");

        chars = buffer.toArrayOmitStartingLineSeparator();
        assertEquals(6, chars.length);
        assertEquals('\t', chars[0]);
        assertEquals('\t', chars[5]);

        buffer = new LexerCharArrayWriter(3);
        buffer.append("\n\n\t")
                .append("\t\t\t");

        chars = buffer.toArrayOmitStartingLineSeparator();
        assertEquals(5, chars.length);
        assertEquals('\n', chars[0]);
        assertEquals('\t', chars[4]);

        buffer = new LexerCharArrayWriter(3);
        buffer.append("\r\n\t")
                .append("\t\t\t");

        chars = buffer.toArrayOmitStartingLineSeparator();
        assertEquals(4, chars.length);
        assertEquals('\t', chars[0]);
        assertEquals('\t', chars[3]);

        buffer = new LexerCharArrayWriter(3);
        buffer.append("\r\n\n")
                .append("\t\t\t");

        buffer.trimRightAfterLastLineSeparator();

        chars = buffer.toArrayOmitStartingLineSeparator();
        assertEquals(1, chars.length);
        assertEquals('\n', chars[0]);

        //
        buffer = new LexerCharArrayWriter(3);
        buffer.append("\r\n\t")
                .append("\t\t\t");

        buffer.trimRightAfterLastLineSeparator();
        chars = buffer.toArrayOmitStartingLineSeparator();
        assertEquals(0, chars.length);
    }
}
