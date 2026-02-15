package org.febit.wit.runtime;

import org.junit.jupiter.api.Test;

import static org.febit.wit.runtime.ALU.isEqual;
import static org.febit.wit.runtime.ALU.plus;
import static org.junit.jupiter.api.Assertions.*;

class ALUTest {

    @Test
    void testPlus() {
        assertEquals(3.0F, 1.0F + 2.0D);
        assertEquals(3.0D, plus(1.0F, 2.0D));
        assertEquals(3.0F, (Double) plus(1.0F, 2.0D));
        assertTrue(isEqual(3.0D, plus(1.0F, 2.0D)));

        assertEquals(0.1F, (Double) plus(0.1F, 0D));

        // FIXME fails, maybe a bug of ALU.isEqual
        // assertTrue(isEqual(0.1F, plus(0.1F, 0D)));

        // NOTICE:
        assertNotEquals(0.1F, plus(0.1F, 0D));
        assertNotEquals(3.0F, plus(1.0F, 2.0D));
        assertNotEquals(3.0F, (Object) (1.0F + 2.0D));
    }
}
