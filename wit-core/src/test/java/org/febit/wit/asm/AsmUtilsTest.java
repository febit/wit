// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.asm;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AsmUtilsTest {

    static String getInternalName(final Class<?> c) {
        return AsmUtils.getInternalName(c.getName());
    }

    @Test
    void getInternalName() {
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
    void test_getBoxedInternalName() {
        assertEquals("java/lang/Integer", AsmUtils.getBoxedInternalName(Integer.class));
        assertEquals("java/lang/String", AsmUtils.getBoxedInternalName(String.class));
        assertEquals("java/util/Map", AsmUtils.getBoxedInternalName(Map.class));

        assertEquals("java/lang/Integer", AsmUtils.getBoxedInternalName(int.class));
        assertEquals("java/lang/Boolean", AsmUtils.getBoxedInternalName(boolean.class));
        assertEquals("java/lang/Void", AsmUtils.getBoxedInternalName(Void.TYPE));
        assertEquals("java/lang/Void", AsmUtils.getBoxedInternalName(Void.class));

        assertEquals("[I", AsmUtils.getBoxedInternalName(int[].class));
        assertEquals("[[I", AsmUtils.getBoxedInternalName(int[][].class));
        assertEquals("[[[I", AsmUtils.getBoxedInternalName(int[][][].class));
    }

    @Test
    void getDescriptor() {

        assertEquals("I", AsmUtils.getDescriptor(int.class));
        assertEquals("J", AsmUtils.getDescriptor(long.class));
        assertEquals("Z", AsmUtils.getDescriptor(boolean.class));
        assertEquals("B", AsmUtils.getDescriptor(byte.class));
        assertEquals("C", AsmUtils.getDescriptor(char.class));

        assertEquals("Ljava/lang/Integer;", AsmUtils.getDescriptor(Integer.class));
        assertEquals("Ljava/lang/String;", AsmUtils.getDescriptor(String.class));
        assertEquals("Ljava/util/Map;", AsmUtils.getDescriptor(Map.class));

        assertEquals("[I", AsmUtils.getDescriptor(int[].class));
        assertEquals("[[I", AsmUtils.getDescriptor(int[][].class));
        assertEquals("[[[I", AsmUtils.getDescriptor(int[][][].class));

        assertEquals("[Ljava/lang/Integer;", AsmUtils.getDescriptor(Integer[].class));
        assertEquals("[[Ljava/lang/Integer;", AsmUtils.getDescriptor(Integer[][].class));
        assertEquals("[[[Ljava/lang/Integer;", AsmUtils.getDescriptor(Integer[][][].class));
    }

}
