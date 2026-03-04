// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.extern.asm;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AsmUtilsTest {

    static String toInternalName(final Class<?> c) {
        return AsmUtils.toInternalName(c.getName());
    }

    @Test
    void toInternalName() {
        assertEquals("java/lang/Integer", toInternalName(Integer.class));
        assertEquals("java/lang/String", toInternalName(String.class));
        assertEquals("java/util/Map", toInternalName(Map.class));

        assertEquals("int", toInternalName(int.class));
        assertEquals("boolean", toInternalName(boolean.class));
        assertEquals("void", toInternalName(Void.TYPE));
        assertEquals("java/lang/Void", toInternalName(Void.class));

        assertEquals("[I", toInternalName(int[].class));
        assertEquals("[[I", toInternalName(int[][].class));
        assertEquals("[[[I", toInternalName(int[][][].class));

        assertEquals("[Ljava/lang/Integer;", toInternalName(Integer[].class));
        assertEquals("[[Ljava/lang/Integer;", toInternalName(Integer[][].class));
        assertEquals("[[[Ljava/lang/Integer;", toInternalName(Integer[][][].class));

    }

    @Test
    void testToBoxedInternalName() {
        assertEquals("java/lang/Integer", AsmUtils.toBoxedInternalName(Integer.class));
        assertEquals("java/lang/String", AsmUtils.toBoxedInternalName(String.class));
        assertEquals("java/util/Map", AsmUtils.toBoxedInternalName(Map.class));

        assertEquals("java/lang/Integer", AsmUtils.toBoxedInternalName(int.class));
        assertEquals("java/lang/Boolean", AsmUtils.toBoxedInternalName(boolean.class));
        assertEquals("java/lang/Void", AsmUtils.toBoxedInternalName(Void.TYPE));
        assertEquals("java/lang/Void", AsmUtils.toBoxedInternalName(Void.class));

        assertEquals("[I", AsmUtils.toBoxedInternalName(int[].class));
        assertEquals("[[I", AsmUtils.toBoxedInternalName(int[][].class));
        assertEquals("[[[I", AsmUtils.toBoxedInternalName(int[][][].class));
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
