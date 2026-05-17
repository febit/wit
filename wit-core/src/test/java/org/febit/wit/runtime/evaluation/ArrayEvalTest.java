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
class ArrayEvalTest {

    @Test
    void objectArrayPropertyRead() {
        ok("""
                var arr = ["wit"];
                var size = arr.length;
                var empty = arr.isEmpty;
                var first = arr[0];
                """);

        error("var arr = []; var value = arr[null];",
                "property/index should not be null for array access.");

        error("var arr = []; var value = arr.name;",
                "Unsupported property for array access: name");

        error("var arr = []; var value = arr[0];",
                "index out of bounds: 0");
    }

    @Test
    void objectArrayPropertyWrite() {
        ok("""
                var arr = [null];
                arr[0] = "wit";
                """);

        error("var arr = [null]; arr[\"name\"] = \"wit\";",
                "property/index should be a number for array access.");

        error("var arr = [null]; arr[1] = \"wit\";",
                "index out of bounds: 1");
    }

    @Test
    void primitiveArrayPropertyWrite() {
        ok("""
                var newIntArray = native [] int;
                var arr = newIntArray(1);
                arr[0] = 1;
                """);

        error("""
                var newIntArray = native [] int;
                var arr = newIntArray(1);
                arr["name"] = 1;
                """, "property/index should be a number for array access.");

        error("""
                var newCharArray = native [] char;
                var arr = newCharArray(1);
                arr[0] = "ab";
                """, "CharSequence value for char should have length of 1");
    }
}

