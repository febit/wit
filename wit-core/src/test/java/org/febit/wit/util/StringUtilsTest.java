// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    @Test
    void toArray() {
        assertArrayEquals(new String[]{"abc", "def", "g"}, StringUtils.toArray("abc,def,g"));
        assertArrayEquals(new String[]{"abc", "def", "g"}, StringUtils.toArray(" abc , def , g "));
        assertArrayEquals(new String[]{"abc", "def", "g"}, StringUtils.toArray("\t\n abc\n\r \n, def \n,,, g ,,, "));

        assertArrayEquals(new String[]{"a", "b", "c"}, StringUtils.toArray("a,b,c"));
        assertSame(ArrayUtils.emptyStrings(), StringUtils.toArray("\t\n ,,,  "));
        assertSame(ArrayUtils.emptyStrings(), StringUtils.toArray(","));
        assertSame(ArrayUtils.emptyStrings(), StringUtils.toArray(",, ,,"));
        assertSame(ArrayUtils.emptyStrings(), StringUtils.toArray("   "));
        assertSame(ArrayUtils.emptyStrings(), StringUtils.toArray(null));
    }

    @Test
    void format() {
        assertEquals("\\abcd", StringUtils.format("\\ab{}cd{1}"));
        assertEquals("ab-123-cd-456|ab-456-cd-123|ab-{1}-cd-123|ab-\\456-cd-123|\\\\123",
                StringUtils.format("ab-{}-cd-{}|ab-{1}-cd-{0}|ab-\\{1}-cd-{0}|ab-\\\\{1}-cd-{0}|\\\\\\\\{0}", 123, 456));
    }
}
