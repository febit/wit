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
class IncludeEvalTest {

    @Test
    void includePathValidation() {
        ok("""
                var script = "string:<% var value = 1; %>";
                include script;
                """);

        error("include null;", "Script path should not be null.");

        error("include \"cases:/__missing__/include-runtime-eval.wit\";",
                "unexpected error: No such resource:",
                "org/febit/wit/scripts/cases/__missing__/include-runtime-eval.wit");
    }

    @Test
    void includeWrapsNestedRuntimeError() {
        error("""
                var script = "string:<% throw \\\"boom\\\"; %>";
                include script;
                """, "boom");

        error("""
                var script = "string:<% 1(); %>";
                include script;
                """, "not a function");

        error("""
                var script = "string:<% include null; %>";
                include script;
                """, "Script path should not be null.");

        error("""
                        var script = "string:<% include \\\"cases:/__missing__/nested-include-runtime-eval.wit\\\"; %>";
                        include script;
                        """, "unexpected error: No such resource:",
                "org/febit/wit/scripts/cases/__missing__/nested-include-runtime-eval.wit");
    }
}

