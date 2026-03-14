package org.febit.wit.runtime.iter;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class IntAscIterTest {

    @Test
    void empty() {
        var iter = IntAscIter.of(2, 1);
        IterAsserts.empty(iter);
    }

    @Test
    void one() {
        var iter = IntAscIter.of(1, 1);
        assertTrue(iter.hasNext());
        assertEquals(1, iter.next());
        assertThrowsExactly(NoSuchElementException.class, iter::next);
    }

    @Test
    void test() {
        var iter = IntAscIter.of(1, 3);

        assertEquals(-1, iter.index());

        assertTrue(iter.hasNext());
        assertEquals(1, iter.next());
        assertEquals(0, iter.index());

        assertTrue(iter.hasNext());
        assertEquals(2, iter.next());
        assertEquals(1, iter.index());

        assertTrue(iter.hasNext());
        assertEquals(3, iter.next());
        assertEquals(2, iter.index());

        assertFalse(iter.hasNext());
        assertThrowsExactly(NoSuchElementException.class, iter::next);
        assertEquals(2, iter.index());
    }

}
