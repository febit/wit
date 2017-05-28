// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.test.tmpls;

import java.util.HashMap;
import java.util.Map;
import org.febit.wit.Context;
import org.febit.wit.EngineManager;
import org.febit.wit.Template;
import org.febit.wit.exceptions.ResourceNotFoundException;
import org.febit.wit.io.impl.DiscardOut;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author zqq90
 */
public class ConetextTest {

    @Test
    public void test() throws ResourceNotFoundException {

        Template template = EngineManager.getTemplate("contextTest.wit");

        Context context = template.merge(new DiscardOut());

        Map map = new HashMap();

        assertEquals("a", context.get("a"));

        context.exportTo(map);
        assertTrue(map.containsKey("a"));
        assertTrue(map.containsKey("b"));
        assertFalse(map.containsKey("c"));

        assertEquals("a", map.get("a"));
        assertEquals("b", map.get("b"));
    }
}
