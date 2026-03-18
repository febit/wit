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

import static org.junit.jupiter.api.Assertions.*;

class ClassNameRopeTest {

    @Test
    void test() {
        ClassNameRope rope;

        rope = new ClassNameRope("abc");
        assertEquals("abc", rope.simpleName());
        assertEquals("abc", rope.componentName());
        assertEquals("abc", rope.toString());

        rope = new ClassNameRope("abc").increaseArrayDepth();
        assertEquals("abc", rope.simpleName());
        assertEquals("abc", rope.componentName());
        assertEquals("abc[]", rope.toString());

        rope = new ClassNameRope("abc").append("def");
        assertEquals("def", rope.simpleName());
        assertEquals("abc.def", rope.componentName());
        assertEquals("abc.def", rope.toString());

        rope = new ClassNameRope("abc").append("def").increaseArrayDepth();
        assertEquals("def", rope.simpleName());
        assertEquals("abc.def", rope.componentName());
        assertEquals("abc.def[]", rope.toString());

        rope = new ClassNameRope("abc").append("def").increaseArrayDepth().increaseArrayDepth();
        assertEquals("def", rope.simpleName());
        assertEquals("abc.def", rope.componentName());
        assertEquals("abc.def[][]", rope.toString());
    }
}
