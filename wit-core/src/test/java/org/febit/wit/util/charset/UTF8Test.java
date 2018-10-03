// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util.charset;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author zqq90
 */
public class UTF8Test {

    public boolean matchEncode(String string) {
        byte[] buffer = new byte[string.length() * UTF8.MAX_BYTES_PER_CHAR];
        byte[] expr = string.getBytes(StandardCharsets.UTF_8);
        int used = UTF8.encode(buffer, string.toCharArray(), 0, string.length());
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

    public boolean matchDencode(String string) {
        char[] expr = string.toCharArray();
        byte[] src = string.getBytes(StandardCharsets.UTF_8);
        char[] buffer = new char[src.length];
        int used = UTF8.decode(src, 0, src.length, buffer);
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

    @Test
    public void testEncode() throws UnsupportedEncodingException {
        assertTrue(matchEncode(""));
        assertTrue(matchEncode("1234567890Az"));
        assertTrue(matchEncode("中文"));
        assertTrue(matchEncode("┯┰┱╂╁┾"));
        assertTrue(matchEncode("ⅠⅡⅢⅣ㈣㈢⒈⒐⑽"));
        assertTrue(matchEncode("\u0080\u00CA\u01CA\u07FF"));
        assertTrue(matchEncode("\uD856\uDC65\uD866\uDC66"));
        assertTrue(matchEncode("\uD700\uE000"));
    }

    @Test
    public void testDecode() throws UnsupportedEncodingException {
        assertTrue(matchDencode(""));
        assertTrue(matchDencode("1234567890Az"));
        assertTrue(matchDencode("中文"));
        assertTrue(matchDencode("┯┰┱╂╁┾"));
        assertTrue(matchDencode("ⅠⅡⅢⅣ㈣㈢⒈⒐⑽"));
        assertTrue(matchDencode("\u0080\u00CA\u01CA\u07FF"));
        assertTrue(matchDencode("\uD856\uDC65\uD866\uDC66"));
        assertTrue(matchDencode("\uD700\uE000"));

    }
}
