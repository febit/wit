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
package org.febit.wit.util;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.support.ReflectionSupport;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ClassMapTest {

    @Test
    void empty() {
        var map = new ClassMap<String>();
        assertNull(map.get(String.class));
        assertNull(map.get(Integer.class));
        assertEquals(0, map.size());
    }

    @Test
    void putAndGet() {
        var map = new ClassMap<String>(1);

        map.putIfAbsent(String.class, "string");
        map.putIfAbsent(Integer.class, "integer");

        assertEquals(2, map.size());
        assertEquals("string", map.get(String.class));
        assertEquals("integer", map.get(Integer.class));

        map.putIfAbsent(String.class, "string2");
        map.putIfAbsent(Integer.class, "integer3");

        assertEquals(2, map.size());
        assertEquals("string", map.get(String.class));
        assertEquals("integer", map.get(Integer.class));

        assertEquals("string", map.unsafeGet(String.class));
        assertEquals("integer", map.unsafeGet(Integer.class));

        map.putIfAbsent(Double.class, "double");
        assertEquals(3, map.size());

        map.putIfAbsent(Float.class, "float");
        assertEquals(4, map.size());

        map.putIfAbsent(Object.class, "object");
        map.putIfAbsent(Object.class, "object");
        assertEquals(5, map.size());

        assertEquals("double", map.unsafeGet(Double.class));
        assertEquals("float", map.unsafeGet(Float.class));
        assertEquals("object", map.unsafeGet(Object.class));
        assertEquals("double", map.get(Double.class));
        assertEquals("float", map.get(Float.class));
        assertEquals("object", map.get(Object.class));
    }

    @Test
    void large() {
        var classes = new ArrayList<>();
        ReflectionSupport.streamAllClassesInPackage("org.febit.wit", ClassUtils::isPublic, c -> true)
                .forEach(classes::add);

        assertTrue(classes.size() > 200);

        var map = new ClassMap<String>(4);
        var i = 0;

        for (; i < 16; i++) {
            var cls = (Class<?>) classes.get(i);
            map.putIfAbsent(cls, cls.getName());
        }
        assertEquals(16, map.size());
        for (int j = 0; j < 16; j++) {
            var cls = (Class<?>) classes.get(j);
            assertEquals(cls.getName(), map.get(cls));
        }

        for (; i < 32; i++) {
            var cls = (Class<?>) classes.get(i);
            map.putIfAbsent(cls, cls.getName());
        }
        assertEquals(32, map.size());
        for (int j = 0; j < 32; j++) {
            var cls = (Class<?>) classes.get(j);
            assertEquals(cls.getName(), map.get(cls));
        }

        for (; i < 200; i++) {
            var cls = (Class<?>) classes.get(i);
            map.putIfAbsent(cls, cls.getName());
        }
        assertEquals(200, map.size());
        for (int j = 0; j < 200; j++) {
            var cls = (Class<?>) classes.get(j);
            assertEquals(cls.getName(), map.get(cls));
        }
    }

}
