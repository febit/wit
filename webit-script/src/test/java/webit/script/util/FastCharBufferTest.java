// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Zqq
 */
public class FastCharBufferTest {

    @Test
    public void trimRightBlankLineTest() {
        FastCharBuffer buffer;

        //
        buffer = new FastCharBuffer(3);
        buffer.append("\t\t\t")
                .append("\t\t\t");
        buffer.trimRightBlankToNewLine();
        assertEquals(1, buffer.index());
        assertEquals(3, buffer.offset());
        assertEquals(6, buffer.size());
        
        
        //
        buffer = new FastCharBuffer(3);
        buffer.append("abc")
                .append("def");
        buffer.trimRightBlankToNewLine();
        assertEquals(6, buffer.size());
        
        //
        buffer = new FastCharBuffer(3);
        buffer.append("\n\t\t")
                .append("\t\t\t");
        buffer.trimRightBlankToNewLine();
        assertEquals(1, buffer.size());
        
        //
        buffer = new FastCharBuffer(3);
        buffer.append("\r\n\t")
                .append("\t\t\t");
        buffer.trimRightBlankToNewLine();
        assertEquals(2, buffer.size());

        //
        buffer = new FastCharBuffer(3);
        buffer.append("\t\n\n")
                .append("\t\t\t");
        buffer.trimRightBlankToNewLine();
        assertEquals("\t\n\n",buffer.toString());
        //
        
        
        char [] chars;
        buffer = new FastCharBuffer(3);
        buffer.append("\t\n\n")
                .append("\t\t\t");
        
        chars = buffer.toArraySkipIfLeftNewLine();
        assertEquals(6, chars.length);
        assertEquals('\t', chars[0]);
        assertEquals('\t', chars[5]);
        
        
        buffer = new FastCharBuffer(3);
        buffer.append("\n\n\t")
                .append("\t\t\t");
        
        chars = buffer.toArraySkipIfLeftNewLine();
        assertEquals(5, chars.length);
        assertEquals('\n', chars[0]);
        assertEquals('\t', chars[4]);
        
        buffer = new FastCharBuffer(3);
        buffer.append("\r\n\t")
                .append("\t\t\t");
        
        chars = buffer.toArraySkipIfLeftNewLine();
        assertEquals(4, chars.length);
        assertEquals('\t', chars[0]);
        assertEquals('\t', chars[3]);
        
        
        buffer = new FastCharBuffer(3);
        buffer.append("\r\n\n")
                .append("\t\t\t");
        
        
        buffer.trimRightBlankToNewLine();
        
        chars = buffer.toArraySkipIfLeftNewLine();
        assertEquals(1, chars.length);
        assertEquals('\n', chars[0]);
        
        //
        buffer = new FastCharBuffer(3);
        buffer.append("\r\n\t")
                .append("\t\t\t");
        
        buffer.trimRightBlankToNewLine();
        chars = buffer.toArraySkipIfLeftNewLine();
        assertEquals(0, chars.length);
    }
}
