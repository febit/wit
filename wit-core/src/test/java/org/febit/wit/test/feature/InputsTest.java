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
package org.febit.wit.test.feature;

import org.febit.wit.Vars;
import org.febit.wit.exception.NoSuchSourceException;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.util.Map;

import static org.febit.wit.WitTestSupport.script;
import static org.junit.jupiter.api.Assertions.*;

class InputsTest {

    @Test
    void echo() throws NoSuchSourceException {
        var writer = new StringWriter();
        var code = """
                code:
                var message;
                echo message;
                """;
        script(code).eval(Vars.of(
                "message", "Hello"
        ), writer);
        assertEquals("Hello", writer.toString());
    }

    @Test
    void plus() throws NoSuchSourceException {
        var writer = new StringWriter();
        var code = """
                code:
                var a,b;
                echo a + b;
                """;
        script(code).eval(Vars.of(Map.of(
                "a", 1,
                "b", 2
        )), writer);
        assertEquals("3", writer.toString());
    }
}
