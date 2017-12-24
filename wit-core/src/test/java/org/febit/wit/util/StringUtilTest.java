// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author zqq90
 */
public class StringUtilTest {

    @Test
    public void toArray() {
        assertArrayEquals(new String[]{"abc", "def", "g"}, StringUtil.toArray("abc,def,g"));
        assertArrayEquals(new String[]{"abc", "def", "g"}, StringUtil.toArray(" abc , def , g "));
        assertArrayEquals(new String[]{"abc", "def", "g"}, StringUtil.toArray("\t\n abc\n\r \n, def \n,,, g ,,, "));

        assertArrayEquals(new String[]{"a", "b", "c"}, StringUtil.toArray("a,b,c"));
        assertSame(ArrayUtil.emptyStrings(), StringUtil.toArray("\t\n ,,,  "));
        assertSame(ArrayUtil.emptyStrings(), StringUtil.toArray(","));
        assertSame(ArrayUtil.emptyStrings(), StringUtil.toArray(",, ,,"));
        assertSame(ArrayUtil.emptyStrings(), StringUtil.toArray("   "));
        assertSame(ArrayUtil.emptyStrings(), StringUtil.toArray(null));
    }

    @Test
    public void format() {
        assertEquals("\\abcd", StringUtil.format("\\ab{}cd{1}"));
        assertEquals("ab-123-cd-456|ab-456-cd-123|ab-{1}-cd-123|ab-\\456-cd-123|\\\\123",
                StringUtil.format("ab-{}-cd-{}|ab-{1}-cd-{0}|ab-\\{1}-cd-{0}|ab-\\\\{1}-cd-{0}|\\\\\\\\{0}", 123, 456));
    }
}
