/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
