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
package org.febit.wit.ir.support;

import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.Undefined;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import static org.febit.wit.ir.support.ALU.greater;
import static org.febit.wit.ir.support.ALU.greaterEqual;
import static org.febit.wit.ir.support.ALU.isEqual;
import static org.febit.wit.ir.support.ALU.isTruly;
import static org.febit.wit.ir.support.ALU.less;
import static org.febit.wit.ir.support.ALU.lessEqual;
import static org.febit.wit.ir.support.ALU.plus;
import static org.febit.wit.ir.support.ALU.size;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({
        "java:S5961", // Test methods should not contain too many assertions
})
class ALUTest {

    @Test
    void testCompareUnsupportedTypes() {
        assertThrows(ScriptEvaluateException.class, () -> greater("abc", "def"));
        assertThrows(ScriptEvaluateException.class, () -> greater(new Object(), new Object()));
        assertThrows(ScriptEvaluateException.class, () -> greater(List.of(1), List.of(1)));

        assertThrows(ScriptEvaluateException.class, () -> greaterEqual("abc", "def"));
        assertThrows(ScriptEvaluateException.class, () -> less("abc", "def"));
        assertThrows(ScriptEvaluateException.class, () -> lessEqual("abc", "def"));
    }

    @Test
    void testIsTruly() {
        assertFalse(isTruly(Undefined.UNDEFINED));
        assertTrue(isTruly(new Object()));

        assertFalse(isTruly(false));
        assertFalse(isTruly(null));
        assertFalse(isTruly(""));
        assertFalse(isTruly(0));
        assertFalse(isTruly(-0));
        assertFalse(isTruly(0.0F));
        assertFalse(isTruly(0.0D));
        assertFalse(isTruly(new int[0]));
        assertFalse(isTruly(new String[0]));
        assertFalse(isTruly(List.of()));
        assertFalse(isTruly(Set.of()));
        assertFalse(isTruly(Map.of()));
        assertFalse(isTruly(Collections.emptyIterator()));
        assertFalse(isTruly((Iterable<Object>) Collections::emptyIterator));
        assertFalse(isTruly(new Vector<>()));
        assertFalse(isTruly(new Vector<>().elements()));

        assertTrue(isTruly(true));
        assertTrue(isTruly(1));
        assertTrue(isTruly(-1));
        assertTrue(isTruly(0.1));
        assertTrue(isTruly(-0.1));
        assertTrue(isTruly("abc"));
        assertTrue(isTruly(new int[]{1}));
        assertTrue(isTruly(new String[]{"a"}));
        assertTrue(isTruly(List.of(1)));
        assertTrue(isTruly(Set.of(1)));
        assertTrue(isTruly(Map.of("a", 1)));
        assertTrue(isTruly(List.of(1).iterator()));
        assertTrue(isTruly((Iterable<Integer>) () -> List.of(1).iterator()));
        assertTrue(isTruly(new Vector<>(List.of(1))));
        assertTrue(isTruly(new Vector<>(List.of(1)).elements()));
    }

    @Test
    void testSize() {
        assertEquals(-1, size(-1));
        assertEquals(-1, size(0));
        assertEquals(-1, size(0));
        assertEquals(-1, size(true));
        assertEquals(-1, size(false));
        assertEquals(-1, size(new Object()));
        assertEquals(-1, size(Collections.emptyIterator()));

        assertEquals(0, size(null));
        assertEquals(0, size(new int[0]));
        assertEquals(0, size(new String[0]));
        assertEquals(0, size(List.of()));
        assertEquals(0, size(Set.of()));
        assertEquals(0, size(Map.of()));
        assertEquals(0, size(""));

        assertEquals(10, size(new int[10]));
        assertEquals(5, size(new String[]{"a", "b", "c", "d", "e"}));
        assertEquals(3, size(new Object[]{1, 2, 3}));

        assertEquals(2, size(Map.of("a", 1, "b", 2)));
        assertEquals(3, size(Set.of(1, 2, 3)));
        assertEquals(4, size(List.of(1, 2, 3, 4)));
        assertEquals(5, size("Hello"));
    }

    @Test
    void testFloatingPointArithmeticPrecisionIssues() {
        assertTrue(isEqual(3.0D, plus(1.0F, 2.0D)));
        assertTrue(isEqual(3.0F, plus(1.0F, 2.0D)));
        assertTrue(isEqual(0.1F, plus(0.1F, 0D)));
        assertTrue(isEqual(0.1D, plus(0.1F, 0D)));

        assertEquals(0.1D, plus(0.1F, 0D));
        assertEquals(3.0D, plus(1.0F, 2.0D));

        // NOTICE: the following assertions are expected to fail due to precision issues with floating-point arithmetic
        assertNotEquals(0.1F, plus(0.1F, 0D));
        assertNotEquals(0.1F, (Double) plus(0.1F, 0D));
        assertNotEquals(3.0F, plus(1.0F, 2.0D));
        assertNotEquals(3.0F, (Object) (1.0F + 2.0D));
    }
}
