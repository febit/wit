// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author zqq90
 */
public class StringUtilTest {

    @Test
    public void cutFieldNameTest() {
        assertEquals("a", StringUtil.cutFieldName("getA", 3));
        assertEquals("ab", StringUtil.cutFieldName("getAb", 3));
        assertEquals("AB", StringUtil.cutFieldName("getAB", 3));
        assertEquals("ABc", StringUtil.cutFieldName("getABc", 3));
        assertEquals("ABC", StringUtil.cutFieldName("getABC", 3));
        assertEquals("aB", StringUtil.cutFieldName("getaB", 3));
    }
}
