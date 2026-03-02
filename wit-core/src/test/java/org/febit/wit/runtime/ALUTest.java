package org.febit.wit.runtime;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.febit.wit.runtime.ALU.isEqual;
import static org.febit.wit.runtime.ALU.plus;
import static org.febit.wit.runtime.ALU.size;
import static org.junit.jupiter.api.Assertions.*;

class ALUTest {

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
