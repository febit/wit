// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.asm;

import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author zqq90
 */
public class ASMUtilTest {

    static String getInternalName(final Class<?> c) {
        return ASMUtil.getInternalName(c.getName());
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
        assertEquals("java/lang/Integer", ASMUtil.getBoxedInternalName(Integer.class));
        assertEquals("java/lang/String", ASMUtil.getBoxedInternalName(String.class));
        assertEquals("java/util/Map", ASMUtil.getBoxedInternalName(Map.class));

        assertEquals("java/lang/Integer", ASMUtil.getBoxedInternalName(int.class));
        assertEquals("java/lang/Boolean", ASMUtil.getBoxedInternalName(boolean.class));
        assertEquals("java/lang/Void", ASMUtil.getBoxedInternalName(Void.TYPE));
        assertEquals("java/lang/Void", ASMUtil.getBoxedInternalName(Void.class));

        assertEquals("[I", ASMUtil.getBoxedInternalName(int[].class));
        assertEquals("[[I", ASMUtil.getBoxedInternalName(int[][].class));
        assertEquals("[[[I", ASMUtil.getBoxedInternalName(int[][][].class));
    }

    @Test
    public void getDescriptor() {

        assertEquals("I", ASMUtil.getDescriptor(int.class));
        assertEquals("J", ASMUtil.getDescriptor(long.class));
        assertEquals("Z", ASMUtil.getDescriptor(boolean.class));
        assertEquals("B", ASMUtil.getDescriptor(byte.class));
        assertEquals("C", ASMUtil.getDescriptor(char.class));

        assertEquals("Ljava/lang/Integer;", ASMUtil.getDescriptor(Integer.class));
        assertEquals("Ljava/lang/String;", ASMUtil.getDescriptor(String.class));
        assertEquals("Ljava/util/Map;", ASMUtil.getDescriptor(Map.class));

        assertEquals("[I", ASMUtil.getDescriptor(int[].class));
        assertEquals("[[I", ASMUtil.getDescriptor(int[][].class));
        assertEquals("[[[I", ASMUtil.getDescriptor(int[][][].class));

        assertEquals("[Ljava/lang/Integer;", ASMUtil.getDescriptor(Integer[].class));
        assertEquals("[[Ljava/lang/Integer;", ASMUtil.getDescriptor(Integer[][].class));
        assertEquals("[[[Ljava/lang/Integer;", ASMUtil.getDescriptor(Integer[][][].class));
    }

}
