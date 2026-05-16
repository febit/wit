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
class FunctionCallEvalTest {

    @Test
    void directCall() {
        ok("""
                var func = function(value) {
                    return value + 1;
                };
                var result = func(1);
                """);

        // calling a plain value as a function should fail at runtime
        error("1();", "not a function");
    }

    @Test
    void nestedCallResult() {
        ok("""
                var factory = function() {
                    return function(value) {
                        return value;
                    };
                };
                var result = factory()(1);
                """);

        // calling the non-function result of another function should fail at runtime
        error("""
                var factory = function() {
                    return 1;
                };
                factory()();
                """, "not a function");
    }
}
