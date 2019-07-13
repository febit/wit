// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author zqq90
 */
class ClassUtilTest {

    @Test
    void getClassByNameTest() throws ClassNotFoundException {

        assertSame(int.class, ClassUtil.getClass("int", 0));
        assertSame(int[].class, ClassUtil.getClass("int", 1));
        assertSame(int[][].class, ClassUtil.getClass("int", 2));

        assertSame(Map.class, ClassUtil.getClass("java.util.Map", 0));
        assertSame(Map[].class, ClassUtil.getClass("java.util.Map", 1));
        assertSame(Map[][].class, ClassUtil.getClass("java.util.Map", 2));

    }
}
