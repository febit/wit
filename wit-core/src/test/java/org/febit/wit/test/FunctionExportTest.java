// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test;

import org.febit.wit.TestWit;
import org.febit.wit.exception.NoSuchFunctionException;
import org.febit.wit.exception.NoSuchSourceException;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

class FunctionExportTest {

    @Test
    void test() throws NoSuchSourceException {

        var context = TestWit.script("/func-export.wit").eval();

        var plus = context.exportFunction("plus");
        assertNotNull(plus);

        assertEquals(1, plus.apply(0, 1));
        assertEquals(5, plus.apply(2, 3));
        assertEquals(1, plus.apply(-2, 3));

        var counter = context.exportFunction("counter");
        assertNotNull(counter);

        assertEquals(0, counter.apply());
        assertEquals(1, counter.apply());
        assertEquals(2, counter.apply());

        var counter2 = context.exportFunction("counter2");
        assertNotNull(counter2);

        assertEquals(0, counter2.apply());
        assertEquals(1, counter2.apply());
        assertEquals(2, counter2.apply());

        var str_len = context.exportFunction("str_len");
        assertNotNull(str_len);

        assertEquals(0, str_len.apply(""));
        assertEquals(1, str_len.apply("a"));
        assertEquals(4, str_len.apply("abcd"));

        var print = context.exportFunction("print");
        StringWriter writer;

        assertNotNull(print);

        writer = new StringWriter();
        print.applyWithOut(writer, "");
        assertEquals("", writer.toString());

        writer = new StringWriter();
        print.applyWithOut(writer, "hello function");
        assertEquals("hello function", writer.toString());

        // Exception cases:
        NoSuchFunctionException exception;

        exception = assertThrows(NoSuchFunctionException.class,
                () -> context.exportFunction("noSuchFunction"));
        assertEquals("No such function: null", exception.getMessage());

        exception = assertThrows(NoSuchFunctionException.class,
                () -> context.exportFunction("count"));
        assertEquals("No such function: java.lang.Integer", exception.getMessage());
    }
}
