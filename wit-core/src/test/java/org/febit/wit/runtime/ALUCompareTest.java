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
package org.febit.wit.runtime;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

import static org.febit.wit.runtime.ALU.greater;
import static org.febit.wit.runtime.ALU.isEqual;
import static org.febit.wit.runtime.ALU.isNotEqual;
import static org.junit.jupiter.api.Assertions.*;

class ALUCompareTest {

    private final List<Object> ones = List.of(
            (byte) 1,
            '\001',
            (short) 1,
            1,
            1L,
            1.0F,
            1.0D,
            new BigInteger("1"),
            new BigDecimal("1"),
            new AtomicInteger(1),
            new AtomicLong(1),
            new LongAccumulator(Long::sum, 1L),
            new LongAdder() {{
                add(1L);
            }},
            new DoubleAdder() {{
                add(1.0D);
            }}
    );

    private final List<Object> twos = List.of(
            (byte) 2,
            '\002',
            (short) 2,
            2,
            2L,
            2.0F,
            2.0D,
            new BigInteger("2"),
            new BigDecimal("2"),
            new AtomicInteger(2),
            new AtomicLong(2),
            new LongAccumulator(Long::sum, 2L),
            new LongAdder() {{
                add(2L);
            }},
            new DoubleAdder() {{
                add(2.0D);
            }}
    );

    @Test
    void testEqualityNonNumeric() {
        assertTrue(isEqual(null, null));
        assertTrue(isEqual("abc", "abc"));
        assertTrue(isEqual(List.of(1, 2), List.of(1, 2)));
        assertTrue(isEqual(Set.of(1, 2), Set.of(1, 2)));
        assertTrue(isEqual(Map.of("a", 1), Map.of("a", 1)));

        assertFalse(isEqual(null, new Object()));
        assertFalse(isEqual(new Object(), null));
        assertFalse(isEqual("abc", "def"));
        assertFalse(isEqual(List.of(1, 2), List.of(2, 3)));
        assertFalse(isEqual(Set.of(1, 2), Set.of(3, 4)));
        assertFalse(isEqual(Map.of("a", 1), Map.of("b", 2)));

        assertTrue(isNotEqual("abc", "def"));
        assertFalse(isNotEqual("abc", "abc"));
    }

    @Test
    void testEquality() {
        for (var one : ones) {
            for (var anotherOne : ones) {
                assertTrue(isEqual(one, anotherOne), "Expected " + one + " to be equal to " + anotherOne);
            }
        }
        for (var one : ones) {
            for (var two : twos) {
                assertFalse(isEqual(one, two), "Expected " + one + " to not be equal to " + two);
            }
        }
    }

    @Test
    void testNotEqual() {
        for (var one : ones) {
            for (var two : twos) {
                assertTrue(ALU.isNotEqual(one, two), "Expected " + one + " to not be equal to " + two);
            }
        }
        for (var one : ones) {
            for (var anotherOne : ones) {
                assertFalse(ALU.isNotEqual(one, anotherOne), "Expected " + one + " to be equal to " + anotherOne);
            }
        }
    }

    @Test
    void testGreater() {
        for (var one : ones) {
            for (var two : twos) {
                assertTrue(greater(two, one), "Expected " + two + " to be greater than " + one);
                assertFalse(greater(one, two), "Expected " + one + " to not be greater than " + two);
            }
        }
        for (var one : ones) {
            for (var anotherOne : ones) {
                assertFalse(greater(one, anotherOne), "Expected " + one + " to not be greater than " + anotherOne);
            }
        }
    }

    @Test
    void testGreaterEqual() {
        for (var one : ones) {
            for (var two : twos) {
                assertTrue(ALU.greaterEqual(two, one), "Expected " + two + " to be greater than or equal to " + one);
                assertFalse(ALU.greaterEqual(one, two),
                        "Expected " + one + " to not be greater than or equal to " + two);
            }
        }
        for (var one : ones) {
            for (var anotherOne : ones) {
                assertTrue(ALU.greaterEqual(one, anotherOne),
                        "Expected " + one + " to be greater than or equal to " + anotherOne);
            }
        }
    }

    @Test
    void testLess() {
        for (var one : ones) {
            for (var two : twos) {
                assertTrue(ALU.less(one, two), "Expected " + one + " to be less than " + two);
                assertFalse(ALU.less(two, one), "Expected " + two + " to not be less than " + one);
            }
        }
        for (var one : ones) {
            for (var anotherOne : ones) {
                assertFalse(ALU.less(one, anotherOne), "Expected " + one + " to not be less than " + anotherOne);
            }
        }
    }

    @Test
    void testLessEqual() {
        for (var one : ones) {
            for (var two : twos) {
                assertTrue(ALU.lessEqual(one, two), "Expected " + one + " to be less than or equal to " + two);
                assertFalse(ALU.lessEqual(two, one), "Expected " + two + " to not be less than or equal to " + one);
            }
        }
        for (var one : ones) {
            for (var anotherOne : ones) {
                assertTrue(ALU.lessEqual(one, anotherOne),
                        "Expected " + one + " to be less than or equal to " + anotherOne);
            }
        }
    }
}
