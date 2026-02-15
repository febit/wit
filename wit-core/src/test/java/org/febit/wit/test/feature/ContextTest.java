// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.feature;

import org.febit.wit.Context;
import org.febit.wit.EngineManager;
import org.febit.wit.Script;
import org.febit.wit.exception.SourceNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ContextTest {

    @Test
    void test() throws SourceNotFoundException {

        Script script = EngineManager.script("contextTest.wit");

        Context context = script.eval();

        Map<String, Object> map = new HashMap<>();

        assertEquals("a", context.heap().get("a"));

        context.heap().exportTo(map);
        assertTrue(map.containsKey("a"));
        assertTrue(map.containsKey("b"));
        assertFalse(map.containsKey("c"));

        assertEquals("a", map.get("a"));
        assertEquals("b", map.get("b"));
    }
}
