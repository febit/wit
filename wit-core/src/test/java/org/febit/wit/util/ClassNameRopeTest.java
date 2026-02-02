// Copyright (c) 2013-present, febit.org. All Rights Reserved.
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
