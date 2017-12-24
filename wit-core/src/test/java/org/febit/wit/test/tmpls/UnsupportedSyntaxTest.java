// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.tmpls;

import org.febit.wit.EngineManager;
import org.febit.wit.exceptions.ParseException;
import org.febit.wit.exceptions.ResourceNotFoundException;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author zqq90
 */
public class UnsupportedSyntaxTest {

    @Test
    public void testNumberParse() throws ResourceNotFoundException {

        // int/long
        assertNull(catchParseException("var x = 0"));
        assertNull(catchParseException("var x = 1000000000"));
        assertNull(catchParseException("var x = 1000000000L"));
        assertNull(catchParseException("var x = 10000000000L"));
        assertNull(catchParseException("var x = 2147483646"));
        assertNull(catchParseException("var x = 2147483647"));
        assertNull(catchParseException("var x = - 2147483648"));
        assertNull(catchParseException("var x = 2147483649L"));
        assertNull(catchParseException("var x = -2147483649L"));
        assertNull(catchParseException("var x = 0 - 2147483647"));
        assertNull(catchParseException("var x = 0 - - 2147483648"));

        assertNotNull(catchParseException("var x = 10000000000"));
        assertNotNull(catchParseException("var x = 100000000000000000000"));
        assertNotNull(catchParseException("var x = 2147483648"));
        assertNotNull(catchParseException("var x = 2147483649"));
        assertNotNull(catchParseException("var x = -2147483649"));
        assertNotNull(catchParseException("var x = 0 - 2147483648"));
        assertNotNull(catchParseException("var x = 0 - - 2147483649"));

        assertNull(catchParseException("var x = 0x0"));
        assertNull(catchParseException("var x = 0xFFFFFFFF"));
        assertNull(catchParseException("var x = 0xFFFFFFFFL"));
        assertNull(catchParseException("var x = 0x0123456789ABCDEFL"));

        assertNotNull(catchParseException("var x = 0xFFFFFFFFF"));
        assertNotNull(catchParseException("var x = 0x0123456789ABCDEF0L"));

        assertNull(catchParseException("var x = 00"));
        assertNull(catchParseException("var x = 000"));
        assertNull(catchParseException("var x = 076543210"));
        assertNull(catchParseException("var x = 000000000000"));
        assertNull(catchParseException("var x = 000000000007"));
        assertNull(catchParseException("var x = 077777777"));
        assertNull(catchParseException("var x = 0077777777"));
        assertNull(catchParseException("var x = 007777777777"));
        assertNull(catchParseException("var x = 017777777777"));
        assertNull(catchParseException("var x = 027777777777"));
        assertNull(catchParseException("var x = 027777777777"));
        assertNull(catchParseException("var x = 037777777777"));
        assertNull(catchParseException("var x = 00777777777777777777777L"));
        assertNull(catchParseException("var x = 01777777777777777777777L"));

        assertNotNull(catchParseException("var x = 08"));
        assertNotNull(catchParseException("var x = 0017777777777"));
        assertNotNull(catchParseException("var x = 047777777777"));
        assertNotNull(catchParseException("var x = 0177777777770"));
        assertNotNull(catchParseException("var x = 0277777777770"));
        assertNotNull(catchParseException("var x = 02777777777777777777777L"));

    }

    private ParseException catchParseException(String code) throws ResourceNotFoundException {
        try {
            EngineManager.getTemplate("code: \n" + code)
                    .reload();
        } catch (ParseException e) {
            return e;
        }
        return null;
    }

}
