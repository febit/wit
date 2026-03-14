package org.febit.wit.runtime.iter;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class IntDescIterTest {

    @Test
    void empty() {
        var iter = IntDescIter.of(1, 2);

        assertEquals(-1, iter.index());
        assertFalse(iter.hasNext());
        assertEquals(-1, iter.index());
        assertThrowsExactly(NoSuchElementException.class, iter::next);
        assertEquals(-1, iter.index());
    }

    @Test
    void one() {
        var iter = IntDescIter.of(1, 1);

        assertEquals(-1, iter.index());
        assertTrue(iter.hasNext());
        assertEquals(1, iter.next());
        assertEquals(0, iter.index());
        assertFalse(iter.hasNext());
        assertEquals(0, iter.index());
        assertThrowsExactly(NoSuchElementException.class, iter::next);
        assertEquals(0, iter.index());
    }

    @Test
    void test() {
        var iter = IntDescIter.of(3, 1);

        assertEquals(-1, iter.index());

        assertTrue(iter.hasNext());
        assertEquals(3, iter.next());
        assertEquals(0, iter.index());

        assertTrue(iter.hasNext());
        assertEquals(2, iter.next());
        assertEquals(1, iter.index());

        assertTrue(iter.hasNext());
        assertEquals(1, iter.next());
        assertEquals(2, iter.index());

        assertFalse(iter.hasNext());
        assertEquals(2, iter.index());
        assertThrowsExactly(NoSuchElementException.class, iter::next);
        assertEquals(2, iter.index());
    }

}
