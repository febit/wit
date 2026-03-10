// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test;

import org.febit.wit.TestWit;
import org.febit.wit.exception.NoSuchSourceException;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class DebugTest {

    @Test
    void test() throws NoSuchSourceException {
        var script = TestWit.script("/debug.wit");
        script.eval();

        var marks = new HashSet<>();
        var counter = new AtomicInteger(0);

        script.evaluator()
                .breakpointHandler((mark, context, statement, result) -> {
                    marks.add(mark);
                    counter.incrementAndGet();
                })
                .eval();

        assertEquals(18, counter.intValue());
        assertTrue(marks.contains(null));
        assertTrue(marks.contains("p1"));
        assertTrue(marks.contains("p2"));
        assertTrue(marks.contains("p3"));
        assertTrue(marks.contains("p4"));

    }

}
