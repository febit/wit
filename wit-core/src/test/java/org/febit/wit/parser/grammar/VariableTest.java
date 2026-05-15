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
class VariableTest {

    @Test
    void variableLookup() {
        error("echo missing;",
                "No such variable: missing");

        ok("""
                var value;
                {
                    var value;
                }
                """);
    }

    @Test
    void duplicateVariableDeclarationRejected() {
        error("""
                        var value;
                        var value;
                        """,
                "Variable already exists: value");

        error("""
                        const value = 1;
                        var value;
                        """,
                "Variable already exists: value");

        error("""
                        var value;
                        const value = 1;
                        """,
                "Variable already exists: value");
    }

    @Test
    void duplicateFunctionArgumentRejected() {
        error("""
                        var func = function(value, value) {
                        };
                        """,
                "Variable already exists: value");
    }
}

