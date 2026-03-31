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
package org.febit.wit.test;

import org.febit.wit.exception.NoSuchSourceException;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.febit.wit.WitTestSupport.script;
import static org.junit.jupiter.api.Assertions.*;

class ContextTest {

    @Test
    void test() throws NoSuchSourceException {
        var context = script("/context.wit").eval();

        assertEquals("a", context.variables().get("a"));

        var exported = new HashMap<String, @Nullable Object>();
        context.variables().each(exported::put);

        assertTrue(exported.containsKey("a"));
        assertTrue(exported.containsKey("b"));
        assertFalse(exported.containsKey("c"));

        assertEquals("a", exported.get("a"));
        assertEquals("b", exported.get("b"));
    }
}
