package org.febit.wit.runtime.iter;

import lombok.experimental.UtilityClass;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@UtilityClass
public class IterAsserts {

    public static void empty(Iter iter) {
        assertEquals(-1, iter.index());
        assertFalse(iter.hasNext());
        assertThrows(NoSuchElementException.class, iter::next);
        assertEquals(-1, iter.index());
        assertFalse(iter.hasNext());
        assertThrows(NoSuchElementException.class, iter::next);
        assertEquals(-1, iter.index());
    }

    public static void empty(KeyIter iter) {
        assertEquals(-1, iter.index());
        assertFalse(iter.hasNext());
        assertThrowsExactly(NoSuchElementException.class, iter::next);
        assertThrowsExactly(NoSuchElementException.class, iter::value);

        assertEquals(-1, iter.index());
        assertFalse(iter.hasNext());
        assertThrowsExactly(NoSuchElementException.class, iter::next);
        assertThrowsExactly(NoSuchElementException.class, iter::value);
    }

    public static void abc(Iter iter) {
        assertEquals(-1, iter.index());

        assertTrue(iter.hasNext());
        assertEquals("a", iter.next());
        assertEquals(0, iter.index());

        assertTrue(iter.hasNext());
        assertEquals("b", iter.next());
        assertEquals(1, iter.index());

        assertTrue(iter.hasNext());
        assertEquals("c", iter.next());
        assertEquals(2, iter.index());

        assertFalse(iter.hasNext());
        assertThrows(NoSuchElementException.class, iter::next);
        assertEquals(2, iter.index());
        assertFalse(iter.hasNext());
        assertThrows(NoSuchElementException.class, iter::next);
        assertEquals(2, iter.index());
    }

    public static void abcChars(Iter iter) {

        assertEquals(-1, iter.index());

        assertTrue(iter.hasNext());
        assertEquals('a', iter.next());
        assertEquals(0, iter.index());

        assertTrue(iter.hasNext());
        assertEquals('b', iter.next());
        assertEquals(1, iter.index());

        assertTrue(iter.hasNext());
        assertEquals('c', iter.next());
        assertEquals(2, iter.index());

        assertFalse(iter.hasNext());
        assertThrows(NoSuchElementException.class, iter::next);
        assertEquals(2, iter.index());
        assertFalse(iter.hasNext());
        assertThrows(NoSuchElementException.class, iter::next);
        assertEquals(2, iter.index());
    }
}
