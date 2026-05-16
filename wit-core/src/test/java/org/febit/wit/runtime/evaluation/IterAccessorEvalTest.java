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
package org.febit.wit.runtime.evaluation;

import org.junit.jupiter.api.Test;

import static org.febit.wit.runtime.evaluation.EvalSupport.error;
import static org.febit.wit.runtime.evaluation.EvalSupport.ok;

@SuppressWarnings({
        "java:S2699", // Tests should include assertions
})
class IterAccessorEvalTest {

    @Test
    void supportedIterProperties() {
        ok("""
                var iter = 1..2;
                var maybeNull = iter[null];
                var hasNext = iter.hasNext;
                var index = iter.index;
                var isFirst = iter.isFirst;
                var isEven = iter.isEven;
                var isOdd = iter.isOdd;
                var next = iter.next;
                var nextIndex = iter.index;
                var nextFirst = iter.isFirst;
                var nextEven = iter.isEven;
                var nextOdd = iter.isOdd;
                """);
    }

    @Test
    void iterPropertyRead() {
        ok("""
                var iter = 1..2;
                var value = iter.hasNext;
                """);

        error("""
                var iter = 1..2;
                var value = iter.x;
                """, "Unsupported property for Iter: x");
    }

    @Test
    void exhaustedIterNext() {
        ok("""
                var iter = 1..1;
                var value = iter.next;
                """);

        error("""
                var iter = 1..1;
                var first = iter.next;
                var second = iter.next;
                """, "no more next");
    }
}


