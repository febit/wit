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
        buffer.trimRightBlankLine();
        assertEquals(1, buffer.index());
        assertEquals(3, buffer.offset());
        assertEquals(6, buffer.size());
        
        
        //
        buffer = new FastCharBuffer(3);
        buffer.append("abc")
                .append("def");
        buffer.trimRightBlankLine();
        assertEquals(6, buffer.size());
        
        //
        buffer = new FastCharBuffer(3);
        buffer.append("\n\t\t")
                .append("\t\t\t");
        buffer.trimRightBlankLine();
        assertEquals(0, buffer.size());
        
        //
        buffer = new FastCharBuffer(3);
        buffer.append("\r\n\t")
                .append("\t\t\t");
        buffer.trimRightBlankLine();
        assertEquals(0, buffer.size());
        
        
        //
        buffer = new FastCharBuffer(3);
        buffer.append("\r\t\t")
                .append("\t\t\t");
        buffer.trimRightBlankLine();
        assertEquals(0, buffer.size());
        

        //
        buffer = new FastCharBuffer(3);
        buffer.append("\t\r\t")
                .append("\t\t\t");
        buffer.trimRightBlankLine();
        assertEquals("\t",buffer.toString());
        

        //
        buffer = new FastCharBuffer(3);
        buffer.append("\t\r\t")
                .append("\r\t\t");
        buffer.trimRightBlankLine();
        assertEquals("\t\r\t",buffer.toString());
        
        //
        buffer = new FastCharBuffer(3);
        buffer.append("\t\r\t")
                .append("\n\t\t");
        buffer.trimRightBlankLine();
        assertEquals("\t\r\t",buffer.toString());
        
        
        //
        buffer = new FastCharBuffer(3);
        buffer.append("\t\r\t")
                .append("\r\n\t\t");
        buffer.trimRightBlankLine();
        assertEquals("\t\r\t",buffer.toString());
        
        
        //
        buffer = new FastCharBuffer(3);
        buffer.append("\t\r\r")
                .append("\n\t\t");
        buffer.trimRightBlankLine();
        assertEquals("\t\r",buffer.toString());
        
        
        //
        buffer = new FastCharBuffer(3);
        buffer.append("\t\r\t")
                .append("\t\n\t\t");
        buffer.trimRightBlankLine();
        assertEquals("\t\r\t\t",buffer.toString());
        
        
        //
        buffer = new FastCharBuffer(3);
        buffer.append("\t\r\t")
                .append("\t\r\t\t");
        buffer.trimRightBlankLine();
        assertEquals("\t\r\t\t",buffer.toString());
        
        
        //
        buffer = new FastCharBuffer(3);
        buffer.append("\t\r\t")
                .append("\t\r\n\t");
        buffer.trimRightBlankLine();
        assertEquals("\t\r\t\t",buffer.toString());

    }
}
