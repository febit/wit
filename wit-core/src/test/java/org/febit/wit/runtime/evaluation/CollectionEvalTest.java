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
class CollectionEvalTest {

    @Test
    void collectionPropertyRead() {
        ok("""
                var newList = native new java.util.ArrayList();
                var list = newList();
                var value = list.length;
                var empty = list.isEmpty;
                list[0] = "wit";
                var first = list[0];
                """);

        error("""
                var newList = native new java.util.ArrayList();
                var list = newList();
                var value = list[0];
                """, "index out of bounds: 0");

        error("""
                var newList = native new java.util.ArrayList();
                var list = newList();
                var value = list[null];
                """, "property should not be null for collection access.");

        error("""
                var newList = native new java.util.ArrayList();
                var list = newList();
                var value = list["name"];
                """, "unsupported property for collection access: name");
    }

    @Test
    void collectionPropertyWrite() {
        ok("""
                var newList = native new java.util.ArrayList();
                var list = newList();
                list[0] = "wit";
                """);

        error("""
                var newList = native new java.util.ArrayList();
                var list = newList();
                list[null] = "wit";
                """, "property should be a number for collection access.");

        error("""
                var newList = native new java.util.ArrayList();
                var list = newList();
                list["name"] = "wit";
                """, "property should be a number for collection access.");
    }
}

