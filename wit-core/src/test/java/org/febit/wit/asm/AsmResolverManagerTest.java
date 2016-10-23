// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.asm;

import static org.junit.Assert.*;
import org.junit.Test;
import org.febit.wit.exceptions.ScriptRuntimeException;

/**
 *
 * @author zqq90
 */
public class AsmResolverManagerTest {

    public static class Foo {

        public String f1 = "foo:f1";
        private String f2 = "foo:f2";
        public final String f3 = "foo:f3";
        public int f4 = 4;
        private int f5 = 5;
        public String bG = "foo:bG"; // hashcode 3109
        public String af = "foo:af"; // hashcode 3109
        private String unXable = "Unreadable & Unwriteable";
        public final String unwriteable = "Unwriteable";
        private String unreadable = "Unreadable";

        public void setUnreadable(String unreadable) {
            this.unreadable = unreadable;
        }

        public int getF5() {
            return f5;
        }

        public void setF5(int f5) {
            this.f5 = f5;
        }

        public String getF2() {
            return f2;
        }

        public void setF2(String f2) {
            this.f2 = f2;
        }
    }

    private static class Book {

        public String f1 = "f1";
        private String f2 = "f2";
        public int f4 = 4;

        public String getF2() {
            return f2;
        }

        public void setF2(String f2) {
            this.f2 = f2;
        }
    }

    @Test
    public void testPrivateClass() throws Exception {
        Exception exception = null;
        try {
            assertNull(AsmResolverManager.createResolverClass(Book.class).newInstance());
        } catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);
    }

    @Test
    public void test() throws Exception {

        Foo foo = new Foo();

        AsmResolver resolver = (AsmResolver) AsmResolverManager.createResolverClass(Foo.class).newInstance();

        assertEquals(null, resolver.getMatchClass());

        int i = 0;
        assertEquals(resolver.get(foo, "f" + (i + 1)), "foo:f1");
        assertEquals(resolver.get(foo, "f" + (i + 2)), "foo:f2");

        resolver.set(foo, "f1", "new:f1");
        resolver.set(foo, "f2", "new:f2");
        resolver.set(foo, "f4", 8);
        resolver.set(foo, "f5", 8);
        resolver.set(foo, "bG", "new:bG");
        resolver.set(foo, "af", "new:af");

        assertEquals(resolver.get(foo, "f1"), "new:f1");
        assertEquals(resolver.get(foo, "f2"), "new:f2");
        assertEquals(resolver.get(foo, "f3"), "foo:f3");
        assertEquals(resolver.get(foo, "f4"), 8);
        assertEquals(resolver.get(foo, "f5"), 8);
        assertEquals(resolver.get(foo, "bG"), "new:bG");
        assertEquals(resolver.get(foo, "af"), "new:af");

        Exception exception;

        exception = null;
        try {
            resolver.get(foo, "unreadable");
        } catch (ScriptRuntimeException e) {
            exception = e;
        }
        assertNotNull(exception);
        assertEquals("Unreadable property " + Foo.class.getName() + "#unreadable", exception.getMessage());

        exception = null;
        try {
            resolver.set(foo, "unwriteable", "unwriteable");
        } catch (ScriptRuntimeException e) {
            exception = e;
        }
        assertNotNull(exception);
        assertEquals("Unwriteable property " + Foo.class.getName() + "#unwriteable", exception.getMessage());

        exception = null;
        try {
            resolver.set(foo, "unXable", "unXable");
        } catch (ScriptRuntimeException e) {
            exception = e;
        }
        assertNotNull(exception);
        assertEquals("Invalid property " + Foo.class.getName() + "#unXable", exception.getMessage());

        exception = null;
        try {
            resolver.get(foo, "unXable");
        } catch (ScriptRuntimeException e) {
            exception = e;
        }
        assertNotNull(exception);
        assertEquals("Invalid property " + Foo.class.getName() + "#unXable", exception.getMessage());

    }
}
