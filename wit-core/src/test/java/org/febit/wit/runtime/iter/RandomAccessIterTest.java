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

import java.util.List;

class RandomAccessIterTest {

    @Test
    void list() {
        IterAsserts.empty(RandomAccessIter.of(List.of()));
        IterAsserts.abc(RandomAccessIter.of(List.of("a", "b", "c")));
    }

    @Test
    void stringArray() {
        IterAsserts.empty(RandomAccessIter.of(new String[]{}));
        IterAsserts.abc(RandomAccessIter.of(new String[]{"a", "b", "c"}));
    }

    @Test
    void objectArray() {
        IterAsserts.empty(RandomAccessIter.of(new Object[]{}));
        IterAsserts.abc(RandomAccessIter.of(new Object[]{"a", "b", "c"}));
    }

    @Test
    void chars() {
        IterAsserts.empty(RandomAccessIter.of(""));
        IterAsserts.empty(RandomAccessIter.ofArray("".toCharArray()));
        IterAsserts.abcChars(RandomAccessIter.of("abc"));
        IterAsserts.abcChars(RandomAccessIter.ofArray("abc".toCharArray()));
    }

}
