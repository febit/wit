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
class AccessorEvalTest {

    @Test
    void stringPropertyRead() {
        ok("""
                var text = "wit";
                var length = text.length;
                var size = text.size;
                var empty = "".isEmpty;
                """);

        error("var value = \"wit\"[null];", "property should not be null for CharSequence access.");

        error("var value = \"wit\".name;", "unsupported property for CharSequence access: name");
    }

    @Test
    void indexedRead() {
        ok("""
                var value = "wit"[0];
                """);

        // reading a string with an out-of-range index should fail at runtime
        error("var value = \"\"[0];");

        // reading a set with an indexed accessor should fail at runtime
        error("""
                var newSet = native new java.util.HashSet();
                var set = newSet();
                var value = set[0];
                """, "unsupported property for collection access: 0");
    }

    @Test
    void indexedWrite() {
        ok("""
                var map = {};
                map["name"] = "wit";
                """);

        // writing through a string indexed accessor should fail at runtime
        error("\"wit\"[0] = \"W\";");

        // writing through a set indexed accessor should fail at runtime
        error("""
                var newSet = native new java.util.HashSet();
                var set = newSet();
                set[0] = "wit";
                """, "collection should be a List for indexed access.");
    }
}
