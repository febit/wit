package org.febit.wit.runtime;

import org.febit.wit.exception.ScriptEvaluateException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static org.febit.wit.runtime.ALU.lshift;
import static org.febit.wit.runtime.ALU.rshift;
import static org.febit.wit.runtime.ALU.urshift;
import static org.junit.jupiter.api.Assertions.*;

class ALUShiftTest {

    private final List<Object> one = List.of(
            1,
            (byte) 1,
            (short) 1,
            '\001',
            1L,
            1.0F,
            1.2F,
            1.0D,
            1.2D,
            new BigInteger("1"),
            new BigDecimal("1.0"),
            new BigDecimal("1.2")
    );

    private final List<Object> four = List.of(
            4,
            (byte) 4,
            (short) 4,
            '\004',
            4L,
            4.0F,
            4.2F,
            4.0D,
            4.2D,
            new BigInteger("4"),
            new BigDecimal("4.0"),
            new BigDecimal("4.2")
    );

    @Test
    void unsupported() {
        var unsupported = List.of(
                1.0F,
                1.0D,
                new BigDecimal("1.0"),
                "1",
                List.of(1),
                new Object()
        );

        for (var a : unsupported) {
            assertThrows(ScriptEvaluateException.class, () -> lshift(a, 1));
            assertThrows(ScriptEvaluateException.class, () -> rshift(a, 1));
            assertThrows(ScriptEvaluateException.class, () -> urshift(a, 1));
        }

        assertThrows(ScriptEvaluateException.class, () -> lshift(null, 1));
        assertThrows(ScriptEvaluateException.class, () -> rshift(null, 1));
        assertThrows(ScriptEvaluateException.class, () -> urshift(null, 1));

        assertThrows(ScriptEvaluateException.class, () -> lshift(1, null));
        assertThrows(ScriptEvaluateException.class, () -> rshift(1, null));
        assertThrows(ScriptEvaluateException.class, () -> urshift(1, null));

        assertThrows(ScriptEvaluateException.class, () -> lshift(1, "not a number"));
        assertThrows(ScriptEvaluateException.class, () -> rshift(1, "not a number"));
        assertThrows(ScriptEvaluateException.class, () -> urshift(1, "not a number"));

        assertThrows(ScriptEvaluateException.class, () ->
                urshift(new BigInteger("4"), 1)
        );
    }

    @Test
    void testLShift() {

        assertEquals(2, lshift(1, 1));
        assertEquals(4, lshift(1, 2));
        assertEquals(8, lshift(1, 3));
        assertEquals(16, lshift(1, 4));

        assertEquals(2, lshift((byte) 1, 1));
        assertEquals(2, lshift((short) 1, 1));
        assertEquals(2, lshift('\001', 1));
        assertEquals(2L, lshift(1L, 1));
        assertEquals(new BigInteger("2"), lshift(new BigInteger("1"), 1));

        for (var a : one) {
            assertEquals(2, lshift(1, a));
        }
        for (var a : four) {
            assertEquals(3 << 4, lshift(3, a));
        }
    }

    @Test
    void testRShift() {
        assertEquals(0, rshift(1, 1));
        assertEquals(0, rshift(1, 2));
        assertEquals(0, rshift(1, 3));
        assertEquals(0, rshift(1, 4));

        assertEquals(0x7F, rshift(0xFF, 1));
        assertEquals(0x3F, rshift(0xFF, 2));
        assertEquals(0x1F, rshift(0xFF, 3));
        assertEquals(0x0F, rshift(0xFF, 4));

        assertEquals(0xFF800000, rshift(0xFF000000, 1));
        assertEquals(0xFFC00000, rshift(0xFF000000, 2));
        assertEquals(0xFFE00000, rshift(0xFF000000, 3));
        assertEquals(0xFFF00000, rshift(0xFF000000, 4));

        assertEquals(2, rshift((byte) 4, 1));
        assertEquals(2, rshift((short) 4, 1));
        assertEquals(2, rshift('\004', 1));
        assertEquals(2L, rshift(4L, 1));
        assertEquals(new BigInteger("2"), rshift(new BigInteger("4"), 1));

        for (var a : one) {
            assertEquals(1 << 4, rshift(1 << 5, a));
            assertEquals(1 << 1, rshift(1 << 2, a));
            assertEquals(0, rshift(1, a));
        }
        for (var a : four) {
            assertEquals(3, rshift(3 << 4, a));
            assertEquals(1, rshift(3 << 3, a));
            assertEquals(0, rshift(3 << 2, a));
        }
    }

    @Test
    void testURShift() {

        assertEquals(0, urshift(1, 1));
        assertEquals(0, urshift(1, 2));
        assertEquals(0, urshift(1, 3));
        assertEquals(0, urshift(1, 4));

        assertEquals(0x7F, urshift(0xFF, 1));
        assertEquals(0x3F, urshift(0xFF, 2));
        assertEquals(0x1F, urshift(0xFF, 3));
        assertEquals(0x0F, urshift(0xFF, 4));

        assertEquals(0x7F800000, urshift(0xFF000000, 1));
        assertEquals(0x3FC00000, urshift(0xFF000000, 2));
        assertEquals(0x1FE00000, urshift(0xFF000000, 3));
        assertEquals(0x0FF00000, urshift(0xFF000000, 4));

        assertEquals(2, urshift((byte) 4, 1));
        assertEquals(2, urshift((short) 4, 1));
        assertEquals(2, urshift('\004', 1));
        assertEquals(2L, urshift(4L, 1));

        for (var a : one) {
            assertEquals(1 << 4, urshift(1 << 5, a));
            assertEquals(1 << 1, urshift(1 << 2, a));
            assertEquals(0, urshift(1, a));
        }
        for (var a : four) {
            assertEquals(3, urshift(3 << 4, a));
            assertEquals(1, urshift(3 << 3, a));
            assertEquals(0, urshift(3 << 2, a));
        }
    }

}
