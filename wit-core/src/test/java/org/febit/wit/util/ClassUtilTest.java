// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.util;

import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author zqq90
 */
public class ClassUtilTest {

    @Test
    public void getClassByNameTest() throws ClassNotFoundException {

        assertSame(int.class, ClassUtil.getClass("int", 0));
        assertSame(int[].class, ClassUtil.getClass("int", 1));
        assertSame(int[][].class, ClassUtil.getClass("int", 2));

        assertSame(Map.class, ClassUtil.getClass("java.util.Map", 0));
        assertSame(Map[].class, ClassUtil.getClass("java.util.Map", 1));
        assertSame(Map[][].class, ClassUtil.getClass("java.util.Map", 2));

    }
}
