// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.feature;

import org.febit.wit.Context;
import org.febit.wit.Script;
import org.febit.wit.TestWit;
import org.febit.wit.exception.NoSuchSourceException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ContextTest {

    @Test
    void test() throws NoSuchSourceException {

        Script script = TestWit.script("contextTest.wit");

        Context context = script.eval();

        Map<String, Object> map = new HashMap<>();

        assertEquals("a", context.variables().get("a"));

        context.variables().exportTo(map);
        assertTrue(map.containsKey("a"));
        assertTrue(map.containsKey("b"));
        assertFalse(map.containsKey("c"));

        assertEquals("a", map.get("a"));
        assertEquals("b", map.get("b"));
    }
}
