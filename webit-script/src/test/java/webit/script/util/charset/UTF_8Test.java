// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util.charset;

import java.io.UnsupportedEncodingException;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Zqq
 */
public class UTF_8Test {

    public boolean matchEncode(String string) throws UnsupportedEncodingException {
        
        byte[] buffer = new byte[(int) (string.length() * UTF_8.MAX_BYTES_PER_CHAR)];

        byte[] expr = string.getBytes("utf-8");
        
        int used = UTF_8.encode(string.toCharArray(), 0, string.length(), buffer);

        if (used != expr.length) {
            return false;
        }

        for (int i = used - 1; i >= 0; i--) {
            if (expr[i] != buffer[i]) {
                return false;
            }
        }
        return true;
    }

    public boolean matchDencode(String string) throws UnsupportedEncodingException {
        
        char[] expr = string.toCharArray();
        
        byte[] src = string.getBytes("utf-8");
        
        char [] buffer = new char[(int)(src.length * UTF_8.MAX_CHARS_PER_BYTE)];

        int used = UTF_8.decode(src, 0, src.length, buffer);
        
        if (used != expr.length) {
            return false;
        }

        for (int i = used - 1; i >= 0; i--) {
            if (expr[i] != buffer[i]) {
                return false;
            }
        }
        return true;
    }

    //@Test
    public void testEncode() throws UnsupportedEncodingException {
        assertTrue(matchEncode(""));

        assertTrue(matchEncode("1234567890Az"));
        assertTrue(matchEncode("中文"));
        assertTrue(matchEncode("┯┰┱╂╁┾"));
        assertTrue(matchEncode("ⅠⅡⅢⅣ㈣㈢⒈⒐⑽"));

    }
    
    
    //@Test
    public void testDecode() throws UnsupportedEncodingException {
        assertTrue(matchDencode(""));

        assertTrue(matchDencode("1234567890Az"));
        assertTrue(matchDencode("中文"));
        assertTrue(matchDencode("┯┰┱╂╁┾"));
        assertTrue(matchDencode("ⅠⅡⅢⅣ㈣㈢⒈⒐⑽"));

    }
}
