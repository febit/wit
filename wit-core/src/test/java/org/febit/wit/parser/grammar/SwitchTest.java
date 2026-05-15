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
class SwitchTest {

    @Test
    void validYield() {
        ok("""
                var func = function(value) {
                  return switch (value) {
                    default -> {
                      yield 1;
                    }
                  };
                };
                """);
        ok("""
                var x = switch (1) {
                  default -> yield 1;
                };
                """);

    }

    @Test
    void legacySwitchDuplicateCaseRejected() {
        error("""
                        switch (1) {
                          case 1:
                          case 1:
                            ;
                        }
                        """,
                "duplicated case value in one switch");
    }

    @Test
    void legacySwitchMultipleDefaultRejected() {
        error("""
                        switch (1) {
                          default:
                            ;
                          default:
                            ;
                        }
                        """,
                "multi default block in one switch");
    }

    @Test
    void enhancedSwitchDuplicateCaseRejected() {
        error("""
                        switch (1) {
                          case 1 -> 1;
                          case 1 -> 2;
                        }
                        """,
                "duplicated case value in one switch");
    }

    @Test
    void enhancedSwitchTypedBranchUnsupported() {
        error("""
                switch (1) {
                  case String s -> ;
                }
                """, "Syntax error");
    }

    @Test
    void labeledBreakInSwitchExpressionUnsupported() {
        error("""
                var x = switch (1) {
                  default -> break outer;
                };
                """, "Unsupported switch branch for expression");
    }

    @Test
    void breakInSwitchExpressionUnsupported() {
        error("""
                var x = switch (1) {
                  default -> {
                    break;
                  }
                };
                """, "Unsupported switch branch for expression");
    }

    @Test
    void continueInSwitchExpressionUnsupported() {
        error("""
                var x = switch (1) {
                  default -> continue;
                };
                """, "Unsupported switch branch for expression");
        error("""
                var x = switch (1) {
                  default -> { continue; }
                };
                """, "Unsupported switch branch for expression");
        error("""
                outer: while (1) {
                  var x = switch (1) {
                    default -> {
                      continue outer;
                    }
                  };
                }
                """, "Unsupported switch branch for expression");
    }

    @Test
    void returnInSwitchExpressionUnsupported() {
        error("""
                var func = function(value) {
                  return switch (value) {
                    default -> {
                      return 1;
                    }
                  };
                };
                """, "Unsupported switch branch for expression");
    }

}
