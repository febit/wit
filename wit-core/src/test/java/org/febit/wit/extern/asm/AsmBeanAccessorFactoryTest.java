// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.extern.asm;

import lombok.Getter;
import lombok.Setter;
import org.febit.wit.exception.ScriptEvaluateException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AsmBeanAccessorFactoryTest {

    @SuppressWarnings({"unused", "FieldMayBeFinal", "SpellCheckingInspection"})
    public static class Foo {

        public String f1 = "foo:f1";
        @Setter
        @Getter
        private String f2 = "foo:f2";
        public final String f3 = "foo:f3";
        public int f4 = 4;
        @Setter
        @Getter
        private int f5 = 5;
        public String bG = "foo:bG"; // hashcode 3109
        public String af = "foo:af"; // hashcode 3109
        private String unXable = "Unreadable & Unwriteable";
        public final String unwriteable = "Unwriteable";
        @Setter
        private String unreadable = "Unreadable";

    }

    @SuppressWarnings({"unused"})
    private static class Book {

        public String f1 = "f1";
        @Getter
        @Setter
        private String f2 = "f2";
        public int f4 = 4;
    }

    @Test
    void testPrivateClass() {
        assertThrows(Exception.class,
                () -> AsmBeanAccessorFactory.constructAccessorClassFor(Book.class)
                        .getConstructor().newInstance());

    }

    @Test
    void test() throws Exception {
        Foo foo = new Foo();

        var accessor = (AsmBeanAccessor) AsmBeanAccessorFactory.constructAccessorClassFor(Foo.class)
                .getConstructor().newInstance();

        int i = 0;
        assertEquals("foo:f1", accessor.get(foo, "f" + (i + 1)));
        assertEquals("foo:f2", accessor.get(foo, "f" + (i + 2)));

        accessor.set(foo, "f1", "new:f1");
        accessor.set(foo, "f2", "new:f2");
        accessor.set(foo, "f4", 8);
        accessor.set(foo, "f5", 8);
        accessor.set(foo, "bG", "new:bG");
        accessor.set(foo, "af", "new:af");

        assertEquals("new:f1", accessor.get(foo, "f1"));
        assertEquals("new:f2", accessor.get(foo, "f2"));
        assertEquals("foo:f3", accessor.get(foo, "f3"));
        assertEquals(8, accessor.get(foo, "f4"));
        assertEquals(8, accessor.get(foo, "f5"));
        assertEquals("new:bG", accessor.get(foo, "bG"));
        assertEquals("new:af", accessor.get(foo, "af"));

        Exception exception;

        exception = assertThrows(ScriptEvaluateException.class,
                () -> accessor.get(foo, "unreadable"));
        assertEquals("Unreadable property " + Foo.class.getName() + "#unreadable", exception.getMessage());

        exception = assertThrows(ScriptEvaluateException.class,
                () -> accessor.set(foo, "unwriteable", "unwriteable"));
        assertEquals("Readonly property " + Foo.class.getName() + "#unwriteable", exception.getMessage());

        exception = assertThrows(ScriptEvaluateException.class,
                () -> accessor.set(foo, "unXable", "unXable"));
        assertEquals("Invalid property " + Foo.class.getName() + "#unXable", exception.getMessage());

        exception = assertThrows(ScriptEvaluateException.class,
                () -> accessor.get(foo, "unXable"));
        assertEquals("Invalid property " + Foo.class.getName() + "#unXable", exception.getMessage());

    }
}
