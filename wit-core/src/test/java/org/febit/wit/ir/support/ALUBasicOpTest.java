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

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;
import org.febit.wit.exception.ScriptEvaluateException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.febit.wit.ir.support.ALU.div;
import static org.febit.wit.ir.support.ALU.minus;
import static org.febit.wit.ir.support.ALU.minusOne;
import static org.febit.wit.ir.support.ALU.mod;
import static org.febit.wit.ir.support.ALU.multi;
import static org.febit.wit.ir.support.ALU.negative;
import static org.febit.wit.ir.support.ALU.plus;
import static org.febit.wit.ir.support.ALU.plusOne;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({
        "java:S5961", // Test methods should not contain too many assertions
})
class ALUBasicOpTest {

    @Test
    void testUnknownNumbersAsBigDecimal() {
        assertEquals(new BigDecimal("2"), plusOne(new MutableInt(1)));
        assertEquals(new BigDecimal("0"), minusOne(new MutableLong(1)));
    }

    @Test
    void testPlusOne() {
        assertThrows(ScriptEvaluateException.class, () -> plusOne(null));
        assertThrows(ScriptEvaluateException.class, () -> plusOne("abc"));

        assertEquals(1, plusOne(0));

        assertEquals(2, plusOne((byte) 1));
        assertEquals(2, plusOne((short) 1));

        assertEquals(98, plusOne('a'));
        assertInstanceOf(Integer.class, plusOne('a'));

        assertEquals(2, plusOne(1));
        assertEquals(2L, plusOne(1L));
        assertEquals(2.0F, plusOne(1.0F));
        assertEquals(2.0D, plusOne(1.0D));
        assertEquals(new BigInteger("2"), plusOne(new BigInteger("1")));
        assertEquals(new BigDecimal("2"), plusOne(new BigDecimal("1")));
    }

    @Test
    void testMinusOne() {
        assertThrows(ScriptEvaluateException.class, () -> minusOne(null));
        assertThrows(ScriptEvaluateException.class, () -> minusOne("abc"));

        assertEquals(-1, minusOne(0));

        assertEquals(0, minusOne((byte) 1));
        assertEquals(0, minusOne((short) 1));

        assertEquals(96, minusOne('a'));
        assertInstanceOf(Integer.class, minusOne('a'));

        assertEquals(0, minusOne(1));
        assertEquals(0L, minusOne(1L));
        assertEquals(0.0F, minusOne(1.0F));
        assertEquals(0.0D, minusOne(1.0D));
        assertEquals(new BigInteger("0"), minusOne(new BigInteger("1")));
        assertEquals(new BigDecimal("0"), minusOne(new BigDecimal("1")));
    }

    @Test
    void testNegative() {
        assertThrows(ScriptEvaluateException.class, () -> negative(null));
        assertThrows(ScriptEvaluateException.class, () -> negative("abc"));

        assertEquals(0, negative(0));

        assertEquals(-((byte) 1), negative((byte) 1));
        assertEquals(-((short) 1), negative((short) 1));
        assertEquals(-97, negative('a'));

        assertInstanceOf(Integer.class, negative((byte) 1));
        assertInstanceOf(Integer.class, negative((short) 1));
        assertInstanceOf(Integer.class, negative('a'));

        assertEquals(-1, negative(1));
        assertEquals(-1L, negative(1L));
        assertEquals(-1.0F, negative(1.0F));
        assertEquals(-1.0D, negative(1.0D));

        assertEquals(new BigInteger("-1"), negative(new BigInteger("1")));
        assertEquals(new BigDecimal("-1"), negative(new BigDecimal("1")));
    }

    @Test
    void testPlus() {
        assertNull(plus(null, null));
        assertEquals("a", plus(null, "a"));
        assertEquals("a", plus("a", null));

        assertEquals("abcdef", plus("abc", "def"));
        assertEquals("abc1", plus("abc", 1));
        assertEquals("1abc", plus(1, "abc"));

        assertEquals(3, plus('\001', '\002'));
        assertEquals(3, plus((byte) 1, (byte) 2));
        assertEquals(3, plus((short) 1, (short) 2));
        assertEquals(3, plus(1, 2));

        assertEquals(3L, plus(1L, '\002'));
        assertEquals(3L, plus(1L, (byte) 2));
        assertEquals(3L, plus(1L, (short) 2));
        assertEquals(3L, plus(1L, 2));
        assertEquals(3L, plus(1L, 2L));

        assertEquals(3.0F, plus(1.0F, '\002'));
        assertEquals(3.0F, plus(1.0F, (byte) 2));
        assertEquals(3.0F, plus(1.0F, (short) 2));
        assertEquals(3.0F, plus(1.0F, 2));
        assertEquals(3.0F, plus(1.0F, 2.0F));

        assertEquals(3.0D, plus(1.0D, '\002'));
        assertEquals(3.0D, plus(1.0D, (byte) 2));
        assertEquals(3.0D, plus(1.0D, (short) 2));
        assertEquals(3.0D, plus(1.0D, 2));
        assertEquals(3.0D, plus(1.0D, 2L));
        assertEquals(3.0D, plus(1.0D, 2.0F));
        assertEquals(3.0D, plus(1.0D, 2.0D));

        assertEquals(new BigInteger("3"), plus(new BigInteger("1"), '\002'));
        assertEquals(new BigInteger("3"), plus(new BigInteger("1"), (byte) 2));
        assertEquals(new BigInteger("3"), plus(new BigInteger("1"), (short) 2));
        assertEquals(new BigInteger("3"), plus(new BigInteger("1"), 2));
        assertEquals(new BigInteger("3"), plus(new BigInteger("1"), 2L));
        assertEquals(new BigInteger("3"), plus(new BigInteger("1"), new BigInteger("2")));

        assertEquals(new BigDecimal("3.0"), plus(new BigInteger("1"), 2.0F));
        assertEquals(new BigDecimal("3.0"), plus(new BigInteger("1"), 2.0D));

        assertEquals(new BigDecimal("3"), plus(new BigDecimal("1"), '\002'));
        assertEquals(new BigDecimal("3"), plus(new BigDecimal("1"), (byte) 2));
        assertEquals(new BigDecimal("3"), plus(new BigDecimal("1"), (short) 2));
        assertEquals(new BigDecimal("3"), plus(new BigDecimal("1"), 2));
        assertEquals(new BigDecimal("3"), plus(new BigDecimal("1"), 2L));
        assertEquals(new BigDecimal("3"), plus(new BigDecimal("1"), new BigInteger("2")));
        assertEquals(new BigDecimal("3"), plus(new BigDecimal("1"), new BigDecimal("2")));
    }

    @Test
    void testMinus() {
        assertThrows(ScriptEvaluateException.class, () -> minus(null, null));
        assertThrows(ScriptEvaluateException.class, () -> minus("abc", null));
        assertThrows(ScriptEvaluateException.class, () -> minus(null, "def"));
        assertThrows(ScriptEvaluateException.class, () -> minus("abc", "def"));

        assertEquals(0, minus('\002', '\002'));
        assertEquals(-1, minus((byte) 1, (byte) 2));
        assertEquals(-1, minus((short) 1, (short) 2));
        assertEquals(-1, minus(1, 2));

        assertEquals(-1L, minus(1L, '\002'));
        assertEquals(-1L, minus(1L, (byte) 2));
        assertEquals(-1L, minus(1L, (short) 2));
        assertEquals(-1L, minus(1L, 2));
        assertEquals(-1L, minus(1L, 2L));

        assertEquals(-1.0F, minus(1.0F, '\002'));
        assertEquals(-1.0F, minus(1.0F, (byte) 2));
        assertEquals(-1.0F, minus(1.0F, (short) 2));
        assertEquals(-1.0F, minus(1.0F, 2));
        assertEquals(-1.0F, minus(1.0F, 2.0F));

        assertEquals(-1.0D, minus(1.0D, '\002'));
        assertEquals(-1.0D, minus(1.0D, (byte) 2));
        assertEquals(-1.0D, minus(1.0D, (short) 2));
        assertEquals(-1.0D, minus(1.0D, 2));
        assertEquals(-1.0D, minus(1.0D, 2L));
        assertEquals(-1.0D, minus(1.0D, 2.0F));
        assertEquals(-1.0D, minus(1.0D, 2.0D));

        assertEquals(new BigInteger("-1"), minus(new BigInteger("1"), '\002'));
        assertEquals(new BigInteger("-1"), minus(new BigInteger("1"), (byte) 2));
        assertEquals(new BigInteger("-1"), minus(new BigInteger("1"), (short) 2));
        assertEquals(new BigInteger("-1"), minus(new BigInteger("1"), 2));
        assertEquals(new BigInteger("-1"), minus(new BigInteger("1"), 2L));
        assertEquals(new BigInteger("-1"), minus(new BigInteger("1"), new BigInteger("2")));

        assertEquals(new BigDecimal("-1.0"), minus(new BigInteger("1"), 2.0F));
        assertEquals(new BigDecimal("-1.0"), minus(new BigInteger("1"), 2.0D));

        assertEquals(new BigDecimal("-1"), minus(new BigDecimal("1"), '\002'));
        assertEquals(new BigDecimal("-1"), minus(new BigDecimal("1"), (byte) 2));
        assertEquals(new BigDecimal("-1"), minus(new BigDecimal("1"), (short) 2));
        assertEquals(new BigDecimal("-1"), minus(new BigDecimal("1"), 2));
        assertEquals(new BigDecimal("-1"), minus(new BigDecimal("1"), 2L));
        assertEquals(new BigDecimal("-1"), minus(new BigDecimal("1"), new BigInteger("2")));
        assertEquals(new BigDecimal("-1"), minus(new BigDecimal("1"), new BigDecimal("2")));
    }

    @Test
    void testMulti() {
        assertThrows(ScriptEvaluateException.class, () -> multi(null, null));
        assertThrows(ScriptEvaluateException.class, () -> multi("abc", null));
        assertThrows(ScriptEvaluateException.class, () -> multi(null, "def"));
        assertThrows(ScriptEvaluateException.class, () -> multi("abc", "def"));

        assertEquals(6, multi('\003', '\002'));
        assertEquals(6, multi((byte) 3, (byte) 2));
        assertEquals(6, multi((short) 3, (short) 2));
        assertEquals(6, multi(3, 2));

        assertEquals(6L, multi(3L, '\002'));
        assertEquals(6L, multi(3L, (byte) 2));
        assertEquals(6L, multi(3L, (short) 2));
        assertEquals(6L, multi(3L, 2));
        assertEquals(6L, multi(3L, 2L));

        assertEquals(6.0F, multi(3.0F, '\002'));
        assertEquals(6.0F, multi(3.0F, (byte) 2));
        assertEquals(6.0F, multi(3.0F, (short) 2));
        assertEquals(6.0F, multi(3.0F, 2));
        assertEquals(6.0F, multi(3.0F, 2.0F));

        assertEquals(6.0D, multi(3.0D, '\002'));
        assertEquals(6.0D, multi(3.0D, (byte) 2));
        assertEquals(6.0D, multi(3.0D, (short) 2));
        assertEquals(6.0D, multi(3.0D, 2));
        assertEquals(6.0D, multi(3.0D, 2L));
        assertEquals(6.0D, multi(3.0D, 2.0F));
        assertEquals(6.0D, multi(3.0D, 2.0D));

        assertEquals(new BigInteger("6"), multi(new BigInteger("3"), '\002'));
        assertEquals(new BigInteger("6"), multi(new BigInteger("3"), (byte) 2));
        assertEquals(new BigInteger("6"), multi(new BigInteger("3"), (short) 2));
        assertEquals(new BigInteger("6"), multi(new BigInteger("3"), 2));
        assertEquals(new BigInteger("6"), multi(new BigInteger("3"), 2L));
        assertEquals(new BigInteger("6"), multi(new BigInteger("3"), new BigInteger("2")));

        assertEquals(new BigDecimal("6.0"), multi(new BigInteger("3"), 2.0F));
        assertEquals(new BigDecimal("6.0"), multi(new BigInteger("3"), 2.0D));

        assertEquals(new BigDecimal("6"), multi(new BigDecimal("3"), '\002'));
        assertEquals(new BigDecimal("6"), multi(new BigDecimal("3"), (byte) 2));
        assertEquals(new BigDecimal("6"), multi(new BigDecimal("3"), (short) 2));
        assertEquals(new BigDecimal("6"), multi(new BigDecimal("3"), 2));
        assertEquals(new BigDecimal("6"), multi(new BigDecimal("3"), 2L));
        assertEquals(new BigDecimal("6"), multi(new BigDecimal("3"), new BigInteger("2")));
        assertEquals(new BigDecimal("6"), multi(new BigDecimal("3"), new BigDecimal("2")));
    }

    @Test
    void testDiv() {
        assertThrows(ScriptEvaluateException.class, () -> div(null, null));
        assertThrows(ScriptEvaluateException.class, () -> div("abc", null));
        assertThrows(ScriptEvaluateException.class, () -> div(null, "def"));
        assertThrows(ScriptEvaluateException.class, () -> div("abc", "def"));

        assertEquals(1, div((byte) 3, (byte) 2));
        assertEquals(1, div((short) 3, (short) 2));
        assertEquals(1, div(3, (byte) 2));
        assertEquals(1, div(3, (short) 2));
        assertEquals(1, div(3, 2));

        assertEquals(1L, div(3L, 2));
        assertEquals(1.5F, div(3.0F, 2));
        assertEquals(1.5D, div(3.0D, 2));

        assertEquals(new BigInteger("1"), div(new BigInteger("3"), 2));
        assertEquals(new BigDecimal("2"), div(new BigInteger("3"), 2.0F));
        assertEquals(new BigDecimal("2"), div(new BigInteger("3"), 2.0D));

        assertEquals(new BigDecimal("2"), div(new BigDecimal("3"), 2));
        assertEquals(new BigDecimal("1.5"), div(new BigDecimal("3.0"), 2));
        assertEquals(new BigDecimal("1.5"), div(new BigDecimal("3.0"), 2.0D));
    }

    @Test
    void testMod() {
        assertThrows(ScriptEvaluateException.class, () -> mod(null, null));
        assertThrows(ScriptEvaluateException.class, () -> mod("abc", null));
        assertThrows(ScriptEvaluateException.class, () -> mod(null, "def"));
        assertThrows(ScriptEvaluateException.class, () -> mod("abc", "def"));

        assertEquals(1, mod((byte) 3, (byte) 2));
        assertEquals(1, mod((short) 3, (short) 2));
        assertEquals(1, mod(3, (byte) 2));
        assertEquals(1, mod(3, (short) 2));
        assertEquals(1, mod(3, 2));

        assertEquals(1L, mod(3L, 2));
        assertEquals(1.0F, mod(3.0F, 2));
        assertEquals(1.0D, mod(3.0D, 2));

        assertEquals(1.5D % 0.7D, mod(1.5D, 0.7D));

        assertEquals(new BigInteger("1"), mod(new BigInteger("3"), 2));
        assertEquals(new BigDecimal("1.0"), mod(new BigInteger("3"), 2.0F));
        assertEquals(new BigDecimal("1.0"), mod(new BigInteger("3"), 2.0D));

        assertEquals(new BigDecimal("1"), mod(new BigDecimal("3"), 2));
        assertEquals(new BigDecimal("1.0"), mod(new BigDecimal("3.0"), 2));
        assertEquals(new BigDecimal("1.0"), mod(new BigDecimal("3.0"), 2.0D));
        assertEquals(new BigDecimal("0.2"), mod(new BigDecimal("1.6"), 0.7D));
    }
}

