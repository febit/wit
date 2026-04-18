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

import static org.febit.wit.ir.IRTestSupport.args;

class SequenceIterTest {

    @Test
    void list() {
        IterAsserts.empty(SequenceIter.of(List.of()));
        IterAsserts.abc(SequenceIter.of(List.of("a", "b", "c")));
    }

    @Test
    void stringArray() {
        IterAsserts.empty(SequenceIter.of(new String[]{}));
        IterAsserts.abc(SequenceIter.of(new String[]{"a", "b", "c"}));
    }

    @Test
    void objectArray() {
        IterAsserts.empty(SequenceIter.of(args()));
        IterAsserts.abc(SequenceIter.of(args("a", "b", "c")));
    }

    @Test
    void chars() {
        IterAsserts.empty(SequenceIter.of(""));
        IterAsserts.empty(SequenceIter.ofArray("".toCharArray()));
        IterAsserts.abcChars(SequenceIter.of("abc"));
        IterAsserts.abcChars(SequenceIter.ofArray("abc".toCharArray()));
    }

}
