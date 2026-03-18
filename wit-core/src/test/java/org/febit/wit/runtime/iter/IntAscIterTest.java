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
