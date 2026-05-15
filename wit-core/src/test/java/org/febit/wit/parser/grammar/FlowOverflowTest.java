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
class FlowOverflowTest {

    @Test
    void topLevel() {
        ok(";");

        error("yield 1;", "Unhandled control flow");
        error("break;", "Unhandled control flow");
        error("continue;", "Unhandled control flow");
        error("return;", "Unhandled control flow");
    }

    @Test
    void yieldInSwitchStatement() {
        ok("""
                switch (1) {
                  default -> 1;
                }
                """);
        error("""
                switch (1) {
                  default -> yield 1;
                }
                """, "Unhandled control flow");
        error("""
                switch (1) {
                  default -> {
                    yield 1;
                  }
                }
                """, "Unhandled control flow");
    }

    @Test
    void function() {
        ok("""
                var func = function(){
                    return;
                };
                """);
        error("""
                var func = function(){
                    break; //continue
                };
                """, "Unhandled control flow");
        error("""
                var func = function(){
                    continue; //break
                };
                """, "Unhandled control flow");
        error("""
                var func = function(){
                    yield 1;
                };
                """, "Unhandled control flow");
    }

    @Test
    void labelMismatch() {
        ok("""
                outer: while (true) {
                    break outer;
                }
                """);

        error("""
                outer: while (true) {
                    break inner;
                }
                """, "Unhandled control flow");
        error("""
                outer: while (true) {
                    continue inner;
                }
                """);
        error("""
                var func = function(){
                    for(i : 3 .. 4){
                        break notExistLabel;
                    }
                };
                """, "Unhandled control flow");

        error("""
                cantDealLabel:
                switch(2){
                    case 1:
                        continue cantDealLabel;
                    default:
                }
                """, "Unhandled control flow");
    }


    @Test
    void switchStatement() {
        ok("""
                switch (1) {
                  case 1 -> 1;
                  case 2 -> break;
                  case 3 -> throw "error";
                  case 4 -> {
                    throw "error";
                  }
                  default -> { break; }
                }
                """);
        error("""
                switch (1) {
                  default -> continue;
                }
                """, "Unhandled control flow");
        error("""
                switch (1) {
                  default -> { continue; }
                }
                """, "Unhandled control flow");
        error("""
                switch (1) {
                  default -> return 1;
                }
                """, "Unhandled control flow");
        error("""
                switch (1) {
                  default -> { return 1; }
                }
                """, "Unhandled control flow");
        error("""
                switch (1) {
                    case 1 -> {
                        yield "unexpected";
                    }
                    default -> {
                    }
                }
                """, "Unhandled control flow");
    }

}
