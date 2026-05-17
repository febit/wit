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
class OperatorEvalTest {

    @Test
    void nullOperandHandling() {
        ok("var value = 1 + null;");

        error("""
                var source = null;
                var value = -source;
                """, "value is null");

        error("""
                var right = null;
                var value = 1 - right;
                """, "right value is null");

        error("""
                var left = null;
                var value = left - 1;
                """, "left value is null");

        error("""
                var left = null;
                var right = null;
                var value = left - right;
                """, "left & right values are null");
    }

    @Test
    void unsupportedOperandTypes() {
        ok("var value = 1 << 1;");

        error("""
                        var left = "a";
                        var value = left - 1;
                        """,
                "Unsupported type: left [java.lang.String], right [java.lang.Integer]");

        error("""
                        var right = "x";
                        var value = 1 << right;
                        """,
                "value is not a number: java.lang.String");

        error("""
                        var source = "x";
                        var value = ~source;
                        """,
                "Unsupported type: java.lang.String");
    }

    @Test
    void shiftOperandValidation() {
        ok("var value = 4 >>> 1;");

        error("""
                var right = null;
                var value = 1 >> right;
                """, "right value is null");

        error("""
                var left = null;
                var value = left >>> 1;
                """, "left value is null");

        error("""
                var left = 1.0;
                var value = left << 1;
                """, "Unsupported type: left [java.lang.Double], right [java.lang.Integer]");

        error("""
                var left = "4";
                var value = left >> 1;
                """, "Unsupported type: left [java.lang.String], right [java.lang.Integer]");

        error("""
                @import java.math.BigInteger;
                var left = BigInteger::new("4");
                var value = left >>> 1;
                """, "Unsupported type: left [java.math.BigInteger], right [java.lang.Integer]");
    }

    @Test
    void multiplicativeOperandValidation() {
        ok("var value = 6 / 2; var mod = 7 % 3; var multi = 3 * 2;");

        error("""
                var right = null;
                var value = 1 / right;
                """, "right value is null");

        error("""
                var left = null;
                var value = left % 1;
                """, "left value is null");

        error("""
                var left = "a";
                var value = left * 2;
                """, "Unsupported type: left [java.lang.String], right [java.lang.Integer]");

        error("""
                var right = "x";
                var value = 1 / right;
                """, "Unsupported type: left [java.lang.Integer], right [java.lang.String]");

        error("""
                var right = "x";
                var value = 1 % right;
                """, "Unsupported type: left [java.lang.Integer], right [java.lang.String]");
    }
}

