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
class NumbersTest {

    @Test
    void edge() {
        ok("var x = 0");
        ok("var x = 1000000000");
        ok("var x = 1000000000L");
        ok("var x = 10000000000L");
        ok("var x = 2147483646");
        ok("var x = 2147483647");
        ok("var x = - 2147483648");
        ok("var x = 2147483649L");
        ok("var x = -2147483649L");
        ok("var x = 0 - 2147483647");
        ok("var x = 0 - - 2147483648");
    }

    @Test
    void overflow() {
        error("var x = 10000000000", "Number overflow");
        error("var x = 100000000000000000000", "Number overflow");
        error("var x = 2147483648", "Number overflow");
        error("var x = 2147483649", "Number overflow");
        error("var x = -2147483649", "Number overflow");
        error("var x = 0 - - 2147483649", "Number overflow");

        ok("var x = 2147483647");
        ok("var x = 0 - 2147483647");
        error("var x = 0 - 2147483648", "Syntax error");
    }

    @Test
    void hex() {
        ok("var x = 0x0");
        ok("var x = 0xFFFFFFFF");
        ok("var x = 0xFFFFFFFFL");
        ok("var x = 0x0123456789ABCDEFL");

        error("var x = 0xFFFFFFFFF");
        error("var x = 0x0123456789ABCDEF0L");
    }

    @Test
    void octal() {
        ok("var x = 00");
        ok("var x = 000");
        ok("var x = 076543210");
        ok("var x = 000000000000");
        ok("var x = 000000000007");
        ok("var x = 077777777");
        ok("var x = 0077777777");
        ok("var x = 007777777777");
        ok("var x = 017777777777");
        ok("var x = 027777777777");
        ok("var x = 027777777777");
        ok("var x = 037777777777");
        ok("var x = 00777777777777777777777L");
        ok("var x = 01777777777777777777777L");

        error("var x = 08", "Syntax error");
        error("var x = 0017777777777", "Syntax error");
        error("var x = 047777777777", "Syntax error");
        error("var x = 0177777777770", "Syntax error");
        error("var x = 0277777777770", "Syntax error");
        error("var x = 02777777777777777777777L", "Syntax error");
    }
}
