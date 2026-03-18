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
