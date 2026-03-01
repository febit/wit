// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test;

import org.febit.wit.TestWit;
import org.febit.wit.exception.NoSuchSourceException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ContextTest {

    @Test
    void test() throws NoSuchSourceException {
        var context = TestWit.script("/context.wit").eval();

        assertEquals("a", context.variables().get("a"));

        var exported = new HashMap<String, Object>();
        context.variables().exportTo(exported);

        assertTrue(exported.containsKey("a"));
        assertTrue(exported.containsKey("b"));
        assertFalse(exported.containsKey("c"));

        assertEquals("a", exported.get("a"));
        assertEquals("b", exported.get("b"));
    }
}
