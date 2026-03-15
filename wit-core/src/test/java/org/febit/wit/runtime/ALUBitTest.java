package org.febit.wit.runtime;

import org.febit.wit.exception.ScriptEvaluateException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static org.febit.wit.runtime.ALU.bitAnd;
import static org.febit.wit.runtime.ALU.bitNot;
import static org.febit.wit.runtime.ALU.bitOr;
import static org.febit.wit.runtime.ALU.bitXor;
import static org.junit.jupiter.api.Assertions.*;

class ALUBitTest {

    private static final BigInteger BIG_1 = new BigInteger("1");

    @Test
    void testBitNot() {
        assertEquals(-2, bitNot(1));
        assertEquals(-2, bitNot('\001'));
        assertEquals(-2, bitNot((byte) 1));
        assertEquals(-2, bitNot((short) 1));
        assertEquals(-2L, bitNot(1L));
        assertEquals(new BigInteger("-2"), bitNot(BIG_1));
    }

    @Test
    void testBitOr() {
        assertEquals(3, bitOr(1, 2));
        assertEquals(3, bitOr('\001', '\002'));
        assertEquals(3, bitOr((byte) 1, (byte) 2));
        assertEquals(3, bitOr((short) 1, (short) 2));
        assertEquals(3L, bitOr(1L, 2L));
        assertEquals(new BigInteger("3"), bitOr(BIG_1, new BigInteger("2")));

        assertEquals(1, bitOr(1, 1));
        assertEquals(1, bitOr(1, '\001'));
        assertEquals(1, bitOr(1, (byte) 1));
        assertEquals(1, bitOr(1, (short) 1));
        assertEquals(1L, bitOr(1, 1L));
        assertEquals(BigInteger.ONE, bitOr(1, BIG_1));
    }

    @Test
    void testBitAnd() {
        assertEquals(0, bitAnd(1, 2));
        assertEquals(0, bitAnd('\001', '\002'));
        assertEquals(0, bitAnd((byte) 1, (byte) 2));
        assertEquals(0, bitAnd((short) 1, (short) 2));
        assertEquals(0L, bitAnd(1L, 2L));
        assertEquals(BigInteger.ZERO, bitAnd(BIG_1, new BigInteger("2")));

        assertEquals(1, bitAnd(1, 3));
        assertEquals(1, bitAnd(1, '\003'));
        assertEquals(1, bitAnd(1, (byte) 3));
        assertEquals(1, bitAnd(1, (short) 3));
        assertEquals(1L, bitAnd(1, 3L));
        assertEquals(BigInteger.ONE, bitAnd(1, new BigInteger("3")));
    }

    @Test
    void testBitXor() {
        assertEquals(3, bitXor(1, 2));
        assertEquals(3, bitXor('\001', '\002'));
        assertEquals(3, bitXor((byte) 1, (byte) 2));
        assertEquals(3, bitXor((short) 1, (short) 2));
        assertEquals(3L, bitXor(1L, 2L));
        assertEquals(new BigInteger("3"), bitXor(BIG_1, new BigInteger("2")));

        assertEquals(0, bitXor(1, 1));
        assertEquals(0, bitXor(1, '\001'));
        assertEquals(0, bitXor(1, (byte) 1));
        assertEquals(0, bitXor(1, (short) 1));
        assertEquals(0L, bitXor(1, 1L));
        assertEquals(BigInteger.ZERO, bitXor(1, BIG_1));
    }

    @Test
    void nullable() {
        assertThrows(ScriptEvaluateException.class, () -> bitNot(null));

        assertThrows(ScriptEvaluateException.class, () -> bitOr(1, null));
        assertThrows(ScriptEvaluateException.class, () -> bitAnd(1, null));
        assertThrows(ScriptEvaluateException.class, () -> bitXor(1, null));

        assertThrows(ScriptEvaluateException.class, () -> bitOr(null, 1));
        assertThrows(ScriptEvaluateException.class, () -> bitAnd(null, 1));
        assertThrows(ScriptEvaluateException.class, () -> bitXor(null, 1));

        assertThrows(ScriptEvaluateException.class, () -> bitOr(null, null));
        assertThrows(ScriptEvaluateException.class, () -> bitAnd(null, null));
        assertThrows(ScriptEvaluateException.class, () -> bitXor(null, null));
    }

    @Test
    void unsupportedTypes() {
        var unsupported = List.of(
                1.0F,
                1.0D,
                new BigDecimal("1.0"),
                "1",
                List.of(1),
                new Object()
        );

        for (var a : unsupported) {
            assertThrows(ScriptEvaluateException.class, () -> bitNot(a));

            assertThrows(ScriptEvaluateException.class, () -> bitOr(a, a));
            assertThrows(ScriptEvaluateException.class, () -> bitAnd(a, a));
            assertThrows(ScriptEvaluateException.class, () -> bitXor(a, a));

            assertThrows(ScriptEvaluateException.class, () -> bitOr(1, a));
            assertThrows(ScriptEvaluateException.class, () -> bitAnd(1, a));
            assertThrows(ScriptEvaluateException.class, () -> bitXor(1, a));

            assertThrows(ScriptEvaluateException.class, () -> bitOr(BIG_1, a));
            assertThrows(ScriptEvaluateException.class, () -> bitAnd(BIG_1, a));
            assertThrows(ScriptEvaluateException.class, () -> bitXor(BIG_1, a));

            assertThrows(ScriptEvaluateException.class, () -> bitOr(a, 1));
            assertThrows(ScriptEvaluateException.class, () -> bitAnd(a, 1));
            assertThrows(ScriptEvaluateException.class, () -> bitXor(a, 1));
        }
    }
}
