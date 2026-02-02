// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ClassUtilsTest {

    @Test
    void loadByNameTest() throws ClassNotFoundException {

        assertSame(int.class, ClassUtils.loadByName("int", 0));
        assertSame(int[].class, ClassUtils.loadByName("int", 1));
        assertSame(int[][].class, ClassUtils.loadByName("int", 2));

        assertSame(Map.class, ClassUtils.loadByName("java.util.Map", 0));
        assertSame(Map[].class, ClassUtils.loadByName("java.util.Map", 1));
        assertSame(Map[][].class, ClassUtils.loadByName("java.util.Map", 2));

    }
}
