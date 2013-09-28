// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Zqq
 */
public class ArrayTemplateParserTest {

    @Test
    public void test() {
        assertEquals("\\abcd", ArrayTemplateParser.parse("\\ab{}cd{1}"));
        assertEquals("ab-123-cd-456|ab-456-cd-123|ab-{1}-cd-123|ab-\\456-cd-123", 
                ArrayTemplateParser.parse("ab-{}-cd-{}|ab-{1}-cd-{0}|ab-\\{1}-cd-{0}|ab-\\\\{1}-cd-{0}", 123, 456));
    }
}
