// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.asm;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author zqq90
 */
public class AsmUtilTest {

    static String getInternalName(final Class<?> c) {
        return AsmUtil.getInternalName(c.getName());
    }

    @Test
    public void getInternalName() {
        assertEquals("java/lang/Integer", getInternalName(Integer.class));
        assertEquals("java/lang/String", getInternalName(String.class));
        assertEquals("java/util/Map", getInternalName(Map.class));

        assertEquals("int", getInternalName(int.class));
        assertEquals("boolean", getInternalName(boolean.class));
        assertEquals("void", getInternalName(Void.TYPE));
        assertEquals("java/lang/Void", getInternalName(Void.class));

        assertEquals("[I", getInternalName(int[].class));
        assertEquals("[[I", getInternalName(int[][].class));
        assertEquals("[[[I", getInternalName(int[][][].class));

        assertEquals("[Ljava/lang/Integer;", getInternalName(Integer[].class));
        assertEquals("[[Ljava/lang/Integer;", getInternalName(Integer[][].class));
        assertEquals("[[[Ljava/lang/Integer;", getInternalName(Integer[][][].class));

    }

    @Test
    public void test_getBoxedInternalName() {
        assertEquals("java/lang/Integer", AsmUtil.getBoxedInternalName(Integer.class));
        assertEquals("java/lang/String", AsmUtil.getBoxedInternalName(String.class));
        assertEquals("java/util/Map", AsmUtil.getBoxedInternalName(Map.class));

        assertEquals("java/lang/Integer", AsmUtil.getBoxedInternalName(int.class));
        assertEquals("java/lang/Boolean", AsmUtil.getBoxedInternalName(boolean.class));
        assertEquals("java/lang/Void", AsmUtil.getBoxedInternalName(Void.TYPE));
        assertEquals("java/lang/Void", AsmUtil.getBoxedInternalName(Void.class));

        assertEquals("[I", AsmUtil.getBoxedInternalName(int[].class));
        assertEquals("[[I", AsmUtil.getBoxedInternalName(int[][].class));
        assertEquals("[[[I", AsmUtil.getBoxedInternalName(int[][][].class));
    }

    @Test
    public void getDescriptor() {

        assertEquals("I", AsmUtil.getDescriptor(int.class));
        assertEquals("J", AsmUtil.getDescriptor(long.class));
        assertEquals("Z", AsmUtil.getDescriptor(boolean.class));
        assertEquals("B", AsmUtil.getDescriptor(byte.class));
        assertEquals("C", AsmUtil.getDescriptor(char.class));

        assertEquals("Ljava/lang/Integer;", AsmUtil.getDescriptor(Integer.class));
        assertEquals("Ljava/lang/String;", AsmUtil.getDescriptor(String.class));
        assertEquals("Ljava/util/Map;", AsmUtil.getDescriptor(Map.class));

        assertEquals("[I", AsmUtil.getDescriptor(int[].class));
        assertEquals("[[I", AsmUtil.getDescriptor(int[][].class));
        assertEquals("[[[I", AsmUtil.getDescriptor(int[][][].class));

        assertEquals("[Ljava/lang/Integer;", AsmUtil.getDescriptor(Integer[].class));
        assertEquals("[[Ljava/lang/Integer;", AsmUtil.getDescriptor(Integer[][].class));
        assertEquals("[[[Ljava/lang/Integer;", AsmUtil.getDescriptor(Integer[][][].class));
    }

}
