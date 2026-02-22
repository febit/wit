// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.feature;

import org.febit.wit.Context;
import org.febit.wit.Function;
import org.febit.wit.TestWit;
import org.febit.wit.exception.NoSuchFunctionException;
import org.febit.wit.exception.NoSuchSourceException;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

class FunctionExportTest {

    @Test
    void test() throws NoSuchSourceException {

        Context context = TestWit.script("/functionExportTest.wit").eval();

        //plus
        Function plus = context.exportFunction("plus");

        assertNotNull(plus);

        assertEquals(1, plus.apply(0, 1));
        assertEquals(5, plus.apply(2, 3));
        assertEquals(1, plus.apply(-2, 3));

        //counter
        Function counter = context.exportFunction("counter");

        assertNotNull(counter);

        assertEquals(0, counter.apply());
        assertEquals(1, counter.apply());
        assertEquals(2, counter.apply());

        //counter
        Function counter2 = context.exportFunction("counter2");

        assertNotNull(counter2);

        assertEquals(0, counter2.apply());
        assertEquals(1, counter2.apply());
        assertEquals(2, counter2.apply());

        //str_len
        Function str_len = context.exportFunction("str_len");

        assertNotNull(str_len);

        assertEquals(0, str_len.apply(""));
        assertEquals(1, str_len.apply("a"));
        assertEquals(4, str_len.apply("abcd"));

        //print
        Function print = context.exportFunction("print");
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
