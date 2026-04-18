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
package org.febit.wit.runtime.heap;

import org.febit.wit.exception.ScriptEvaluateException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.febit.wit.ir.IRTestSupport.args;
import static org.junit.jupiter.api.Assertions.*;

class GenericHeapTest {

    @Test
    void local() {
        var heap = GenericHeap.local();
        assertFalse(heap.has("a"));
        assertNull(heap.get("a", false));
        heap.set("a", 123);
        assertTrue(heap.has("a"));
        assertEquals(123, heap.get("a", false));
        heap.clear();
        assertFalse(heap.has("a"));
        assertNull(heap.get("a", false));
    }

    @Test
    void concurrent() {
        var heap = GenericHeap.concurrent();
        assertFalse(heap.has("a"));
        assertNull(heap.get("a", false));
        heap.set("a", 123);
        assertTrue(heap.has("a"));
        assertEquals(123, heap.get("a", false));
        heap.clear();
        assertFalse(heap.has("a"));
        assertNull(heap.get("a", false));
    }

    @Test
    void forEach() {
        var heap = GenericHeap.local();
        heap.set("a", 123);
        heap.set("b", 456);

        var map = new HashMap<String, Object>();
        heap.forEach(map::put);
        assertEquals(Map.of("a", 123, "b", 456), map);
    }

    @Test
    void apply() {
        var heap = GenericHeap.local();
        heap.set("a", 123);
        heap.set("b", 456);

        assertEquals(123, heap.apply(args("a")));
        assertEquals(123, heap.get("a"));
        assertEquals(456, heap.apply(args("b")));
        assertEquals(456, heap.get("b"));

        assertNull(heap.apply(args("c")));

        assertEquals("abc", heap.apply(args("a", "abc")));
        assertEquals("abc", heap.get("a"));
        assertEquals("def", heap.apply(args("b", "def")));
        assertEquals("def", heap.get("b"));
        assertEquals("ghi", heap.apply(args("c", "ghi")));
        assertEquals("ghi", heap.get("c"));

        assertThatThrownBy(() -> heap.apply(null))
                .isInstanceOf(ScriptEvaluateException.class)
                .hasMessage("key of heap is required");

        assertThatThrownBy(() -> heap.apply(args()))
                .isInstanceOf(ScriptEvaluateException.class)
                .hasMessage("key of heap is required");

        assertThatThrownBy(() -> heap.apply(new Object[]{null}))
                .isInstanceOf(ScriptEvaluateException.class)
                .hasMessage("key of heap cannot be null");
    }

}
