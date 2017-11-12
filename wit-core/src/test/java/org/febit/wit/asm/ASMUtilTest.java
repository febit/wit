// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.asm;

import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author zqq90
 */
public class ASMUtilTest {

    static String getInternalName(final Class c) {
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
    public void getDescriptor() {

        assertEquals("Ljava/lang/Integer;", ASMUtil.getDescriptor(Integer.class));
        assertEquals("Ljava/lang/String;", ASMUtil.getDescriptor(String.class));
        assertEquals("Ljava/util/Map;", ASMUtil.getDescriptor(Map.class));

        assertEquals("I", ASMUtil.getDescriptor(int.class));
        assertEquals("[I", ASMUtil.getDescriptor(int[].class));
        assertEquals("[[I", ASMUtil.getDescriptor(int[][].class));
        assertEquals("[[[I", ASMUtil.getDescriptor(int[][][].class));

        assertEquals("[Ljava/lang/Integer;", ASMUtil.getDescriptor(Integer[].class));
        assertEquals("[[Ljava/lang/Integer;", ASMUtil.getDescriptor(Integer[][].class));
        assertEquals("[[[Ljava/lang/Integer;", ASMUtil.getDescriptor(Integer[][][].class));
    }

}
