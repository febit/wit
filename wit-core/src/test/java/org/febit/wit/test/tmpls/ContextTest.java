// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.tmpls;

import org.febit.wit.Context;
import org.febit.wit.EngineManager;
import org.febit.wit.Template;
import org.febit.wit.exceptions.ResourceNotFoundException;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author zqq90
 */
public class ContextTest {

    @Test
    public void test() throws ResourceNotFoundException {

        Template template = EngineManager.getTemplate("contextTest.wit");

        Context context = template.merge();

        Map<String, Object> map = new HashMap<>();

        assertEquals("a", context.get("a"));

        context.exportTo(map);
        assertTrue(map.containsKey("a"));
        assertTrue(map.containsKey("b"));
        assertFalse(map.containsKey("c"));

        assertEquals("a", map.get("a"));
        assertEquals("b", map.get("b"));
    }
}
