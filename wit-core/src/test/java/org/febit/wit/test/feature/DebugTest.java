// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.feature;

import org.febit.wit.Script;
import org.febit.wit.TestWit;
import org.febit.wit.exception.NoSuchSourceException;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DebugTest {

    private final Set<Object> labelCache = new HashSet<>();
    private int pointCount = 0;

    @Test
    void test() throws NoSuchSourceException {
        Script script = TestWit.script("/debug.wit");

        script.eval();

        labelCache.clear();
        pointCount = 0;
        script.evaluator()
                .breakpointHandler((label, context, statement, result) -> {
                    labelCache.add(label);
                    pointCount++;
                })
                .eval();

        assertEquals(18, pointCount);
        assertTrue(labelCache.contains(null));
        assertTrue(labelCache.contains("p1"));
        assertTrue(labelCache.contains("p2"));
        assertTrue(labelCache.contains("p3"));
        assertTrue(labelCache.contains("p4"));

    }

}
