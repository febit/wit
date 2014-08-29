// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.asm;

import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author zqq
 */
public class ASMUtilTest {

    @Test
    public void getInternalName() {
        assertEquals("java/lang/Integer", ASMUtil.getInternalName(Integer.class));
        assertEquals("java/lang/String", ASMUtil.getInternalName(String.class));
        assertEquals("java/util/Map", ASMUtil.getInternalName(Map.class));

        assertEquals("int", ASMUtil.getInternalName(int.class));
        assertEquals("boolean", ASMUtil.getInternalName(boolean.class));
        assertEquals("void", ASMUtil.getInternalName(Void.TYPE));
        assertEquals("java/lang/Void", ASMUtil.getInternalName(Void.class));

        assertEquals("[I", ASMUtil.getInternalName(int[].class));
        assertEquals("[[I", ASMUtil.getInternalName(int[][].class));
        assertEquals("[[[I", ASMUtil.getInternalName(int[][][].class));

        assertEquals("[Ljava/lang/Integer;", ASMUtil.getInternalName(Integer[].class));
        assertEquals("[[Ljava/lang/Integer;", ASMUtil.getInternalName(Integer[][].class));
        assertEquals("[[[Ljava/lang/Integer;", ASMUtil.getInternalName(Integer[][][].class));

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
