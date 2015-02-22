// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.security;

import static org.junit.Assert.*;
import org.junit.Test;
import webit.script.security.impl.DefaultNativeSecurityManager;

/**
 *
 * @author Zqq
 */
public class DefaultNativeSecurityManagerTest {

    @Test
    public void test() {

        String list = "-a"
                + ",a"
                + ",+b"
                + "\r\n-b"
                + ", c"
                + "\r-c.d "
                + ", c.d.e.f"
                + ",-c.d.e.f.g";

        DefaultNativeSecurityManager manager = new DefaultNativeSecurityManager();

        manager.setList(list);
        manager.init();

        assertFalse(manager.access("a"));
        assertFalse(manager.access("a.aa"));
        assertFalse(manager.access("a.aa.aaa"));
        
        assertFalse(manager.access("b"));
        assertFalse(manager.access("b.b"));
        assertFalse(manager.access("b.c"));
        
        assertTrue(manager.access("c"));
        assertTrue(manager.access("c.cc"));
        assertTrue(manager.access("c.cc.ccc"));
        assertFalse(manager.access("c.d"));
        assertFalse(manager.access("c.d.e"));
        assertTrue(manager.access("c.d.e.f"));
        assertFalse(manager.access("c.d.e.f.g"));
        assertFalse(manager.access("c.d.e.f.g.h"));

    }
}
