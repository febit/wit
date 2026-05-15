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
package org.febit.wit.parser.grammar;

import org.junit.jupiter.api.Test;

import static org.febit.wit.parser.grammar.GrammarCheckSupport.error;
import static org.febit.wit.parser.grammar.GrammarCheckSupport.ok;

@SuppressWarnings({
        "java:S2699", // Tests should include assertions
})
class ConstTest {

    @Test
    void constExpression() {
        ok("const value = 1 + 2;");
        ok("const values = [1, 2, 3];");
    }

    @Test
    void nonConstValueRejected() {
        error("const value = function() { };",
                "Cannot calculate as const");

        error("""
                        var source;
                        const copy = source;
                        """,
                "Cannot calculate as const");

        error("""
                        var func = function(value = function() { }) {
                        };
                        """,
                "Cannot calculate as const");
    }

    @Test
    void optimizationFailureReportedAsParseError() {
        error("const value = 1 / 0;",
                "Exception occur when do optimization");
    }
}

