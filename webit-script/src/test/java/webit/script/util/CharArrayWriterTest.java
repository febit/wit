// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Zqq
 */
public class CharArrayWriterTest {

    @Test
    public void trimRightBlankLineTest() {
        CharArrayWriter buffer;

        //
        buffer = new CharArrayWriter(3);
        buffer.append("\t\t\t".toCharArray())
                .append("\t\t\t".toCharArray());
        buffer.trimRightBlankToNewLine();
        assertEquals(1, buffer.currentBufferIndex);
        assertEquals(3, buffer.offset);
        assertEquals(6, buffer.size());

        //
        buffer = new CharArrayWriter(3);
        buffer.append("abc".toCharArray())
                .append("def".toCharArray());
        buffer.trimRightBlankToNewLine();
        assertEquals(6, buffer.size());

        //
        buffer = new CharArrayWriter(3);
        buffer.append("\n\t\t".toCharArray())
                .append("\t\t\t".toCharArray());
        buffer.trimRightBlankToNewLine();
        assertEquals(1, buffer.size());

        //
        buffer = new CharArrayWriter(3);
        buffer.append("\r\n\t".toCharArray())
                .append("\t\t\t".toCharArray());
        buffer.trimRightBlankToNewLine();
        assertEquals(2, buffer.size());

        //
        buffer = new CharArrayWriter(3);
        buffer.append("\t\n\n".toCharArray())
                .append("\t\t\t".toCharArray());
        buffer.trimRightBlankToNewLine();
        assertEquals("\t\n\n", buffer.toString());
        //

        char[] chars;
        buffer = new CharArrayWriter(3);
        buffer.append("\t\n\n".toCharArray())
                .append("\t\t\t".toCharArray());

        chars = buffer.toArraySkipIfLeftNewLine();
        assertEquals(6, chars.length);
        assertEquals('\t', chars[0]);
        assertEquals('\t', chars[5]);

        buffer = new CharArrayWriter(3);
        buffer.append("\n\n\t".toCharArray())
                .append("\t\t\t".toCharArray());

        chars = buffer.toArraySkipIfLeftNewLine();
        assertEquals(5, chars.length);
        assertEquals('\n', chars[0]);
        assertEquals('\t', chars[4]);

        buffer = new CharArrayWriter(3);
        buffer.append("\r\n\t".toCharArray())
                .append("\t\t\t".toCharArray());

        chars = buffer.toArraySkipIfLeftNewLine();
        assertEquals(4, chars.length);
        assertEquals('\t', chars[0]);
        assertEquals('\t', chars[3]);

        buffer = new CharArrayWriter(3);
        buffer.append("\r\n\n".toCharArray())
                .append("\t\t\t".toCharArray());

        buffer.trimRightBlankToNewLine();

        chars = buffer.toArraySkipIfLeftNewLine();
        assertEquals(1, chars.length);
        assertEquals('\n', chars[0]);

        //
        buffer = new CharArrayWriter(3);
        buffer.append("\r\n\t".toCharArray())
                .append("\t\t\t".toCharArray());

        buffer.trimRightBlankToNewLine();
        chars = buffer.toArraySkipIfLeftNewLine();
        assertEquals(0, chars.length);
    }
}
