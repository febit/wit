// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.tmpls;

import org.febit.wit.exceptions.ParseException;
import org.junit.jupiter.api.Test;

import static org.febit.wit.EngineManager.codeChecker;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author zqq90
 */
class UnsupportedSyntaxTest {

    @Test
    void testNumberParse() {

        // int/long
        assertDoesNotThrow(codeChecker("var x = 0"));
        assertDoesNotThrow(codeChecker("var x = 1000000000"));
        assertDoesNotThrow(codeChecker("var x = 1000000000L"));
        assertDoesNotThrow(codeChecker("var x = 10000000000L"));
        assertDoesNotThrow(codeChecker("var x = 2147483646"));
        assertDoesNotThrow(codeChecker("var x = 2147483647"));
        assertDoesNotThrow(codeChecker("var x = - 2147483648"));
        assertDoesNotThrow(codeChecker("var x = 2147483649L"));
        assertDoesNotThrow(codeChecker("var x = -2147483649L"));
        assertDoesNotThrow(codeChecker("var x = 0 - 2147483647"));
        assertDoesNotThrow(codeChecker("var x = 0 - - 2147483648"));

        assertThrows(ParseException.class, codeChecker("var x = 10000000000"));
        assertThrows(ParseException.class, codeChecker("var x = 100000000000000000000"));
        assertThrows(ParseException.class, codeChecker("var x = 2147483648"));
        assertThrows(ParseException.class, codeChecker("var x = 2147483649"));
        assertThrows(ParseException.class, codeChecker("var x = -2147483649"));
        assertThrows(ParseException.class, codeChecker("var x = 0 - 2147483648"));
        assertThrows(ParseException.class, codeChecker("var x = 0 - - 2147483649"));

        assertDoesNotThrow(codeChecker("var x = 0x0"));
        assertDoesNotThrow(codeChecker("var x = 0xFFFFFFFF"));
        assertDoesNotThrow(codeChecker("var x = 0xFFFFFFFFL"));
        assertDoesNotThrow(codeChecker("var x = 0x0123456789ABCDEFL"));

        assertThrows(ParseException.class, codeChecker("var x = 0xFFFFFFFFF"));
        assertThrows(ParseException.class, codeChecker("var x = 0x0123456789ABCDEF0L"));

        assertDoesNotThrow(codeChecker("var x = 00"));
        assertDoesNotThrow(codeChecker("var x = 000"));
        assertDoesNotThrow(codeChecker("var x = 076543210"));
        assertDoesNotThrow(codeChecker("var x = 000000000000"));
        assertDoesNotThrow(codeChecker("var x = 000000000007"));
        assertDoesNotThrow(codeChecker("var x = 077777777"));
        assertDoesNotThrow(codeChecker("var x = 0077777777"));
        assertDoesNotThrow(codeChecker("var x = 007777777777"));
        assertDoesNotThrow(codeChecker("var x = 017777777777"));
        assertDoesNotThrow(codeChecker("var x = 027777777777"));
        assertDoesNotThrow(codeChecker("var x = 027777777777"));
        assertDoesNotThrow(codeChecker("var x = 037777777777"));
        assertDoesNotThrow(codeChecker("var x = 00777777777777777777777L"));
        assertDoesNotThrow(codeChecker("var x = 01777777777777777777777L"));

        assertThrows(ParseException.class, codeChecker("var x = 08"));
        assertThrows(ParseException.class, codeChecker("var x = 0017777777777"));
        assertThrows(ParseException.class, codeChecker("var x = 047777777777"));
        assertThrows(ParseException.class, codeChecker("var x = 0177777777770"));
        assertThrows(ParseException.class, codeChecker("var x = 0277777777770"));
        assertThrows(ParseException.class, codeChecker("var x = 02777777777777777777777L"));

    }
}
